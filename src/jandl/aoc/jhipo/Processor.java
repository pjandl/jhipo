/*
 * File:		Processor.java
 * Classname:	Processor
 * 
 * Description:	Processor subsystem that executes instruction on JHIPOVM architecture.
 * 
 * Author:	Peter Jandl Junior
 * Date:		2015-04-30			
 * 
 */
package jandl.aoc.jhipo;

public class Processor {
	public final static int WORD_SIZE = 8; // bits
	public final static int MEMORY_ADDRESS_SIZE = 8; // bits
	public final static int IO_ADDRESS_SIZE = 8; // bits
	public static boolean debug = false;
	public static boolean showMicroOperations = false;
	public static boolean showOpcodeDecode = false;
	public static boolean showPerformance = false;
	public static long maxExecuteLimit = 100000L;
	private long executedInstructions;
	public final static Instruction MNEMONIC[] = { new Instruction("NOP", 0x00, false),
			new Instruction("STA", 0x10, true), new Instruction("LDA", 0x20, true), new Instruction("ADD", 0x30, true),
			new Instruction("SUB", 0x40, true), new Instruction("OR", 0x50, true), new Instruction("AND", 0x60, true),
			new Instruction("NOT", 0x70, false), new Instruction("JMP", 0x80, true), new Instruction("JN", 0x90, true),
			new Instruction("JZ", 0xA0, true), new Instruction("CALL", 0xB0, true), new Instruction("RET", 0xC0, false),
			new Instruction("IN", 0xD0, true), new Instruction("OUT", 0xE0, true),
			new Instruction("HLT", 0xF0, false) };

	private enum ALU_Ops {
		ADD, SUB, OR, AND, NOT, ACCLOAD
	};

	private short PC;
	private short SP;
	private short REM;
	private short RDM;
	private short RI;
	private byte ACC;
	private boolean auxFlag;
	private boolean negativeFlag;
	private boolean zeroFlag;
	private boolean haltFlag;
	private Memory memory;
	private IO io;

	public Processor(Memory memory, IO io) {
		if (memory == null) {
			throw new RuntimeException("[Processor | error] no memory assigned");
		}
		if (io == null) {
			throw new RuntimeException("[Processor | error] no I/O subsystem");
		}
		this.memory = memory;
		this.io = io;
		PC = 0;
		SP = (short) 0xFF;
		REM = 0;
		RDM = 0;
		RI = 0;
		ACC = 0;
		negativeFlag = false;
		zeroFlag = false;
		haltFlag = false;
	}

	public void start() {
		System.out.printf("[Processor] started%s", Tools.NL);
		executedInstructions = 0;
		do {
			fetch();
			decode();
			execute();
			executedInstructions++;
			if (executedInstructions == maxExecuteLimit) {
				System.out.printf("[Processor] maximum execution limit reached: %d instructions%s", maxExecuteLimit,
						Tools.NL);
				haltFlag = true;
			}
		} while (!haltFlag);
		System.out.printf("[Processor] stopped%s", Tools.NL);
		if (showPerformance) {
			System.out.printf("[Processor] executed instructions: %d%s", executedInstructions, Tools.NL);
		}
	}

	private void fetch() {
		if (showMicroOperations) {
			System.out.printf("[Processor] fetch%s", Tools.NL);
		}
		rem_pc();
		readMemory();
		pc_inc();
		ri_rdm();
	}

	private void decode() {
		byte opCode = (byte) (RI & 0xFF);
		int i = MNEMONIC.length - 1;
		for (; i >= 0; i--) {
			if (opCode == MNEMONIC[i].getOpCode()) {
				break;
			}
		}
		if (debug) {
			System.out.printf("[Processor] decode: RI = %02X [mnemonic = %d]%s", opCode, i, Tools.NL);
		}
		if (i < 0) {
			throw new UnsupportedOperationException(
					String.format("[Processor | error] invalid opCode: 0x%02X", opCode));
		}
		if (showOpcodeDecode) {
			System.out.printf("[Processor] decode: %s%s", MNEMONIC[i], Tools.NL);
		}
	}

	private void execute() {
		if (showMicroOperations) {
			System.out.printf("[Processor] execute%s", Tools.NL);
		}
		int opCode = RI & 0xFF;
		switch (opCode) {
		case 0x00: // NOP
			executeNOP();
			break;
		case 0x10: // STA
			executeSTA();
			break;
		case 0x20: // LDA
			executeLDA();
			break;
		case 0x30: // ADD
			executeADD();
			break;
		case 0x40: // SUB
			executeSUB();
			break;
		case 0x50: // OR
			executeOR();
			break;
		case 0x60: // AND
			executeAND();
			break;
		case 0x70: // NOT
			executeNOT();
			break;
		case 0x80: // JMP
			executeJMP();
			break;
		case 0x90: // JN
			executeJN();
			break;
		case 0xA0: // JZ
			executeJZ();
			break;
		case 0xB0: // CALL
			executeCALL();
			break;
		case 0xC0: // RET
			executeRET();
			break;
		case 0xD0: // IN
			executeIN();
			break;
		case 0xE0: // OUT
			executeOUT();
			break;
		case 0xF0: // HLT
			executeHLT();
			break;
		default:
			throw new RuntimeException(
					String.format("[Processor | error] internal error: 0x%02X", opCode));
		}
	}

	private void executeNOP() {
		// Não faz nada
		;
	}

	private void executeSTA() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		rdm_acc();
		writeMemory();
	}

	private void executeLDA() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readMemory();
		acc_rdm();
		check_NZ(ALU_Ops.ACCLOAD);
	}

	private void executeADD() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readMemory();
		alu_op(ALU_Ops.ADD);
		check_NZ(ALU_Ops.ADD);
	}

	private void executeSUB() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readMemory();
		alu_op(ALU_Ops.SUB);
		check_NZ(ALU_Ops.SUB);
	}

	private void executeOR() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readMemory();
		alu_op(ALU_Ops.OR);
		check_NZ(ALU_Ops.OR);
	}

	private void executeAND() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readMemory();
		alu_op(ALU_Ops.AND);
		check_NZ(ALU_Ops.AND);
	}

	private void executeNOT() {
		alu_op(ALU_Ops.NOT);
		check_NZ(ALU_Ops.NOT);
	}

	private void executeJMP() {
		rem_pc();
		readMemory();
		pc_rdm();
	}

	private void executeJN() {
		if (negativeFlag) {
			rem_pc();
			readMemory();
			pc_rdm();
		} else {
			pc_inc();
		}
	}

	private void executeJZ() {
		if (zeroFlag) {
			rem_pc();
			readMemory();
			pc_rdm();
		} else {
			pc_inc();
		}
	}

	private void executeCALL() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_sp();
		rdm_x_pc();
		writeMemory();
		sp_dec();
	}

	private void executeRET() {
		sp_inc();
		rem_sp();
		readMemory();
		pc_rdm();
	}

	private void executeIN() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		readIO();
		acc_rdm();
		check_NZ(ALU_Ops.ACCLOAD);
	}

	private void executeOUT() {
		rem_pc();
		readMemory();
		pc_inc();
		rem_rdm();
		rdm_acc();
		writeIO();
	}

	private void executeHLT() {
		haltFlag = true;
	}

	private void rem_pc() {
		REM = PC;
		if (showMicroOperations) {
			System.out.printf("REM <-- PC         | REM <-- %02X%s", PC, Tools.NL);
		}
	}

	private void readMemory() {
		RDM = memory.read(REM & 0xFF);
		if (showMicroOperations) {
			System.out.printf("RDM <-- MEM[REM]   | RDM <-- %02X <-- MEM[%02X]%s", RDM, REM, Tools.NL);
		}
	}

	private void pc_inc() {
		PC = (short) ((PC + 1) % 0x100);
		if (showMicroOperations) {
			System.out.printf("PC  <-- PC + 1     | PC  <-- %02X%s", PC, Tools.NL);
		}
	}

	private void ri_rdm() {
		RI = RDM;
		if (showMicroOperations) {
			System.out.printf("RI  <-- RDM        | RI  <-- %02X%s", RDM, Tools.NL);
		}
	}

	private void rem_rdm() {
		REM = RDM;
		if (showMicroOperations) {
			System.out.printf("REM <-- RDM        | REM <-- %02X%s", RDM, Tools.NL);
		}
	}

	private void rdm_acc() {
		RDM = ACC;
		if (showMicroOperations) {
			System.out.printf("RDM <-- ACC        | RDM <-- %02X%s", ACC, Tools.NL);
		}
	}

	private void writeMemory() {
		memory.write(REM & 0xFF, (byte)RDM);
		if (showMicroOperations) {
			System.out.printf("MEM[REM] <-- RDM   | MEM[%02X] <-- %02X <-- RDM%s", REM, RDM, Tools.NL);
		}
	}

	private void check_NZ(ALU_Ops op) {
		if (op == ALU_Ops.SUB) {
			negativeFlag = auxFlag;
		} else {
			negativeFlag = ACC < 0;
		}
		zeroFlag = ACC == 0;
		if (showMicroOperations) {
			System.out.printf("N   <-- %1X        | Z   <-- %1X%s", negativeFlag ? 1 : 0, zeroFlag ? 1 : 0, Tools.NL);
		}
	}

	private void acc_rdm() {
		ACC = (byte) RDM;
		if (showMicroOperations) {
			System.out.printf("ACC <-- RDM        | ACC <-- %02X%s", RDM, Tools.NL);
		}
	}

	private void pc_rdm() {
		PC = RDM;
		if (showMicroOperations) {
			System.out.printf("PC  <-- RDM        | PC  <-- %02X%s", RDM, Tools.NL);
		}
	}

	private void rem_sp() {
		REM = SP;
		if (showMicroOperations) {
			System.out.printf("REM <-- SP         | REM <-- %02X%s", SP, Tools.NL);
		}
	}

	private void rdm_x_pc() {
		short aux = RDM;
		RDM = PC;
		PC = aux;
		if (showMicroOperations) {
			System.out.printf("RDM <-> PC         | RDM <-- %02X | PC <-- %02X%s", RDM, PC, Tools.NL);
		}
	}

	private void sp_inc() {
		SP = (short) ((SP + 1) % 0x100);
		if (showMicroOperations) {
			System.out.printf("SP  <-- SP + 1     | SP  <-- %02X%s", SP, Tools.NL);
		}
	}

	private void sp_dec() {
		SP = (short) ((SP - 1) % 0x100);
		if (showMicroOperations) {
			System.out.printf("SP  <-- SP - 1     | SP  <-- %02X%s", SP, Tools.NL);
		}
	}

	private void readIO() {
		RDM = io.read(REM);
		if (showMicroOperations) {
			System.out.printf("RDM <-- IO[REM]    | RDM <-- %02X <-- IO[%02X]%s", RDM, REM, Tools.NL);
		}
	}

	private void writeIO() {
		io.write(REM, (byte)RDM);
		if (showMicroOperations) {
			System.out.printf("IO[REM] <-- RDM    | IO[%02X] <-- %02X <-- RDM%s", REM, RDM, Tools.NL);
		}
	}

	private void alu_op(ALU_Ops op) {
		byte oldACC = (byte)ACC;
		byte res = 0;
		switch (op) {
		case ADD:
			res = (byte)(ACC + (byte)RDM);
			break;
		case AND:
			res = (byte)(ACC & (byte)RDM);
			break;
		case NOT:
			res = (byte)(~ACC);
			break;
		case OR:
			res = (byte)(ACC | (byte)RDM);
			break;
		case SUB:
			res = (byte)(ACC - (byte)RDM);
			break;
		case ACCLOAD:
			throw new RuntimeException("[Processor | error] invalid ALU op");
		}
		auxFlag = res < 0;
		ACC = (byte) (res & 0xFF);
		if (debug) {
			System.out.printf("[Processor | ALU] ACC:%02X | RDM:%02X | Z: %1d | N: %1d%s", ACC, RDM, ACC == 0 ? 1 : 0, auxFlag ? 1 : 0, Tools.NL);
		}
		if (showMicroOperations) {
			if (op == ALU_Ops.NOT) {
				System.out.printf("ACC <-- op AC      | ACC <-- %02X <-- %s ACC[%02X]%s", ACC, op, oldACC, Tools.NL);
			} else {
				System.out.printf("ACC <-- ACC op RDM | ACC <-- %02X <-- ACC[%02X] %s RDM[%02X]%s", ACC, oldACC, op, RDM,
						Tools.NL);
			}
		}
	}

	public ProcessorStatus getProcessorStatus() {
		return new ProcessorStatus(this);
	}

	public class ProcessorStatus {
		public byte PC;
		public byte SP;
		public byte REM;
		public byte RDM;
		public byte RI;
		public byte ACC;
		public boolean N;
		public boolean Z;

		public ProcessorStatus(Processor processor) {
			PC = (byte) (processor.PC & 0xFF);
			SP = (byte) (processor.SP & 0xFF);
			REM = (byte) (processor.REM & 0xFF);
			RDM = (byte) (processor.RDM & 0xFF);
			RI = (byte) (processor.RI & 0xFF);
			ACC = processor.ACC;
			N = processor.negativeFlag;
			Z = processor.zeroFlag;
		}

		public String toString() {
			String status = String.format(
					"[Processor | Status]%sPC:%02X | SP:%02X | RDM:%02X | REM:%02X | RI:%02X | ACC:%02X | Z:%1d | N:%1d%s",
					Tools.NL, PC, SP, RDM, REM, RI, ACC, Z ? 1 : 0, N ? 1 : 0, Tools.NL);
			return status;
		}
	}

}
