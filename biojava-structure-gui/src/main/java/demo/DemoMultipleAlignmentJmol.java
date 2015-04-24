package demo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.SVDSuperimposer;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.align.gui.StructureAlignmentDisplay;
import org.biojava.nbio.structure.align.model.Block;
import org.biojava.nbio.structure.align.model.BlockSet;
import org.biojava.nbio.structure.align.model.MultipleAlignment;
import org.biojava.nbio.structure.align.model.Pose;
import org.biojava.nbio.structure.align.util.AtomCache;

/**
 * Demo for visualizing the results of a Multiple Alignment, from a sample MultipleAlignment object.
 * 
 * @author Aleix Lafita
 * 
 */
public class DemoMultipleAlignmentJmol {

	public static void main(String[] args) throws IOException, StructureException {
		
		//Specify the structures to align
		//List<String> names = Arrays.asList("1tim.a", "1vzw", "1nsj", "3tha.a");	//TIM barrels
		List<String> names = Arrays.asList("1mbc", "1hlb", "1thb.a", "1ith.a");		//globins
		
		//Load the CA atoms of the structures
		AtomCache cache = new AtomCache();
		List<Atom[]> atomArrays = new ArrayList<Atom[]>();
		for (String name:names) atomArrays.add(cache.getAtoms(name));
		
		//Here the multiple structural alignment algorithm comes in place to generate the alignment object
		MultipleAlignment fakeMultAln = fakeMultipleAlignment("globins",atomArrays);
		
		//Complete the information of the MultipleAlignment object
		fakeMultAln.setAlgorithmName("fakeAlgorithm");
		fakeMultAln.setAtomArrays(atomArrays);
		fakeMultAln.setStructureNames(names);
		
		StructureAlignmentDisplay.display(fakeMultAln, atomArrays);
	}
	
	private static MultipleAlignment fakeMultipleAlignment(String family, List<Atom[]>atomArrays) throws StructureException{
		
		//Initialize the multiple alignment
		MultipleAlignment fakeMultAln = new MultipleAlignment();
		BlockSet blockSet = new BlockSet(fakeMultAln);
		Pose pose = new Pose(blockSet);
		Block block = new Block(blockSet);
		fakeMultAln.getBlockSets().add(blockSet);
		blockSet.setPose(pose);
		blockSet.getBlocks().add(block);
		
		int size = atomArrays.size();
		
		if (family == "globins"){
			
			//Alignment obtained from MUSTANG multiple alignment (just some of the residues, not the whole alignment)
			List<Integer> aligned1 = Arrays.asList(0,1,2,3,4,5,6,7,8,9,null,          29,30,31,32,33,34,35,36,37,38,123,124,125,126,127,128,129,130,131,132,133,134);
			List<Integer> aligned2 = Arrays.asList(10,11,12,13,14,15,16,17,18,19,null,39,40,41,42,43,44,45,46,47,48,133,134,135,136,137,138,139,140,141,142,143,144);
			List<Integer> aligned3 = Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,          29,30,31,32,33,34,35,36,37,38,117,118,119,120,121,122,123,124,125,126,127,128);
			List<Integer> aligned4 = Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,          30,31,32,33,34,35,36,37,38,39,121,122,123,124,125,126,127,128,129,130,131,132);
			block.getAlignRes().add(aligned1);
			block.getAlignRes().add(aligned2);
			block.getAlignRes().add(aligned3);
			block.getAlignRes().add(aligned4);
			
			int length = aligned1.size();
			block.setCols(length);
			block.setRows(size);
			
			//We suppose the first molecule as reference and superimpose everything to it
			for (int i=0; i<size; i++){				
				List<Atom> atomSet1 = new ArrayList<Atom>();
				List<Atom> atomSet2 = new ArrayList<Atom>();
				for (int k=0; k<length; k++){
					Integer pos1 = block.getAlignRes().get(0).get(k);
					Integer pos2 = block.getAlignRes().get(i).get(k);
					if (pos1==null || pos2==null) continue;
					atomSet1.add(atomArrays.get(0)[pos1]);
					
					atomSet2.add(atomArrays.get(i)[pos2]);
				}
				SVDSuperimposer svd = new SVDSuperimposer(atomSet1.toArray(new Atom[0]), atomSet2.toArray(new Atom[0]));
				pose.getRotation().add(svd.getRotation());
				pose.getTranslation().add(svd.getTranslation());
			}
			
			blockSet.setLength(block.getCols());
			fakeMultAln.setLength(blockSet.getLength());
			fakeMultAln.setSize(size);
		}
		return fakeMultAln;
	}

}
