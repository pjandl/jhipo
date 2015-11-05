import jandl.aoc.jhipo.JHIPOVM;
import jandl.aoc.jhipo.Processor;
import jandl.aoc.jhipo.Tools;

public class jhipovm {
	// flags
	private static boolean showMemoryMap = true;
	private static boolean executeProgram = true;
	private static boolean showIOMap = false;
	private static boolean showMicroOperations = false;
	private static boolean showOpcodeDecode = false;
	private static boolean showPerformance = false;
	private static boolean showProcessorStatus = true;

	public static void main(String[] args) {
		if (args.length == 0) {
			showHelp(1);
		}
		int i = 0;
		while (i < args.length) {
			if (args[i].charAt(0) == '+') {
				if (args[i].contains("a")) {
					showMemoryMap = true;
				}
				if (args[i].contains("d")) {
					JHIPOVM.DEBUG = true;
				}
				if (args[i].contains("e")) {
					executeProgram = true;
				}
				if (args[i].contains("i")) {
					showIOMap = true;
				}
				if (args[i].contains("m")) {
					showMicroOperations = true;
				}
				if (args[i].contains("o")) {
					showOpcodeDecode = true;
				}
				if (args[i].contains("p")) {
					showPerformance = true;
				}
				if (args[i].contains("s")) {
					showProcessorStatus = true;
				}
				i++;
			} else if (args[i].charAt(0) == '-') {
				if (args[i].contains("a")) {
					showMemoryMap = false;
				}
				if (args[i].contains("d")) {
					JHIPOVM.DEBUG = false;
				}
				if (args[i].contains("e")) {
					executeProgram = false;
				}
				if (args[i].contains("i")) {
					showIOMap = false;
				}
				if (args[i].contains("m")) {
					showMicroOperations = false;
				}
				if (args[i].contains("o")) {
					showOpcodeDecode = false;
				}
				if (args[i].contains("p")) {
					showPerformance = false;
				}
				if (args[i].contains("s")) {
					showProcessorStatus = false;
				}
				i++;
			} else if (args[i].charAt(0) == '?') {
				showHelp(0);
			} else {
				break;
			}
		}
		if (i >= args.length && executeProgram) {
			showHelp(2);
		}
		Processor.showMicroOperations = showMicroOperations;
		Processor.showOpcodeDecode = showOpcodeDecode;
		Processor.showPerformance = showPerformance;
		JHIPOVM vm = null;
		try {
			vm = new JHIPOVM();
			vm.loadMemory(args[i]);
			processShowOptions(vm);
			if (executeProgram) {
				vm.run();
				processShowOptions(vm);
			}
		} catch (Exception e) {
			System.err.printf("[JHIPOVM | error] %s.%s", e.getLocalizedMessage(), Tools.NL);
		} finally {
			try {
				vm.close();
			} catch (Exception e) {
				// apesar do erro, permite finalização
			}
		}
	}

	public static void processShowOptions(JHIPOVM vm) {
		System.out.println();
		if (showMemoryMap) {
			System.out.println(vm.getMemory());
		}
		if (showIOMap) {
			System.out.println(vm.getIO());
		}
		if (showProcessorStatus) {
			System.out.println(vm.getProcessor().getProcessorStatus());
		}
	}

	public static void showHelp(int code) {
		switch (code) {
		case 1:
			System.err.printf("[JHIPOVM | help] no arguments supplied.%s", Tools.NL);
			break;
		case 2:
			System.err.printf("[JHIPOVM | help] no input filename supplied.%s", Tools.NL);
			break;
		}
		System.out.printf("Usage:%s\tjhipovm [options] <fileName>%s", Tools.NL, Tools.NL, Tools.NL);
		System.out.printf("Options (default ON):%s", Tools.NL);
		System.out.printf("\t<+|->a\tmemory map        [ON|off]%s", Tools.NL);
		System.out.printf("\t<+|->e\texecute program   [ON|off]%s", Tools.NL);
		System.out.printf("\t<+|->i\ti/o map           [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->m\tmicroinstructions [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->o\topcode decode     [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->p\tperformance       [on|OFF]%s", Tools.NL);
		System.out.printf("\t<+|->s\tprocessor status  [ON|off]%s", Tools.NL);
		System.exit(-1);
	}

}
