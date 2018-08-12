import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import cmsc420.meeshquest.part2.MeeshQuest;



public class RunInput {

	private static void runFile(File f) {
		if (f.isDirectory()) {
			runFilesInDirectory(f);
		} else {
			String fn = f.getName();
			String fname = "";
			String ext = "";
			int dotInd = fn.lastIndexOf(".");
			
			if (dotInd > 0) {
				fname = fn.substring(0, dotInd);
				ext = fn.substring(dotInd + 1, fn.length());
				if (!ext.equals("xml") && !ext.equals("txt")) {
					System.err.println("Skipping " + fn + 
							": filename extension must be xml or txt");
					return;
				}
			} else {
				System.err.println("Skipping " + fn 
						+ ": no filename or filename extension");
				return;
			}
			
			if (fname.indexOf("output") >= 0) {
				System.err.println("Skipping " + fn 
						+ ": it looks like an output file");
				return;
			}
			
			String outFname = fname.replaceAll("input", "output");
			if (outFname.equals(fname)) {
				outFname += ".output";
			}
			
			try {
				System.err.println("Processing: " + f);
				System.setIn(new FileInputStream(f));
				System.setOut(new PrintStream(new FileOutputStream(
						f.getParentFile().getCanonicalPath() + "/" + outFname + "." + ext)));
				MeeshQuest.main(new String[] {});
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}		
		}
	}
	
	private static void runFilesInDirectory(File path) {
		System.err.println("Current path: " + path);
		File[] fList = path.listFiles();
		for (File f : fList) {
			runFile(f);
		}
		
		System.err.println("done");
	}

	public static void main(String[] args) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		FileNameExtensionFilter filter = new FileNameExtensionFilter(
//		        "Test XML Files", "xml", "txt");
//		fc.setFileFilter(filter);
		int fcResult = fc.showOpenDialog(null);
		if (fcResult == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " +
		            fc.getSelectedFile().getName());
			runFile(fc.getSelectedFile());
		}
		
//		runFile(new File("testfiles/testAll3.input.xml"));
//		runFile(new File("testfiles/testExtraCreditPm1All2.input.xml"));
	}
}
