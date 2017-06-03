package com.bob.game.inputs;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.GameController;

import java.util.*;

public class InputsManager {

    private Rule[] rules;
    private List<Rule> allRules;
    private InputsLayer layer;
    private List<Block[][]> rulePages;
    private int ruleLimit = 8;
    private static final int noRulesPerPage = 4;
    private int curPageIndex = 0;
    private int curRuleIndex = 0;
	private GameController gameController;
	private boolean prevRules = true;
	//private TextButton button;

    public InputsManager() {
        this.rules = new Rule[noRulesPerPage]; 	//rules on one page
        this.allRules = new ArrayList<Rule>();
        this.rulePages = new ArrayList<Block[][]>();

        for (int i = 0; i < rules.length; i++) {
            rules[i] = new Rule();
        }
        
        for (int i = 0; i < 8; i++) {
        	allRules.add(new Rule());
        }
    }

    public void initRuleView(Skin skin, int startingX, int startingY) {
        for (Rule rule : rules) {
            rule.initView(layer, skin, startingX, startingY);
            rule.addClearButton(layer, skin, startingX, startingY, this);
            startingY -= 140;
        }
    }

    public void setLayer(InputsLayer layer) {
        this.layer = layer;
    }

    public boolean checkRules() {
    	upDateAllRules();
        return checkHelper()&prevRules;
    }
    
    public void upDateRule(int index) {
    	Rule rule = allRules.get(index+curRuleIndex);
    	rule.reset();
    	rule.setRuleBlocks(rules[index].getBlockStack());
    }
    
    public void upDateAllRules() {
    	for(int j=0; j<noRulesPerPage; j++) {
    		Rule rule = allRules.get(j+curRuleIndex);
    		rule.reset();
    		rule.setRuleBlocks(rules[j].getBlockStack());
    	}
    }
    
    private boolean checkHelper() {
    	boolean allValid = true;
    	for (Rule rule : rules) {
    		allValid &= (rule.isValid());
    	}
    	return allValid;
    }

    public void resetRules() {
        for (Rule r: rules) {
            r.reset();
        }
        for (Rule r: allRules) {
            r.reset();
        }
        rulePages.clear();
        
        curPageIndex = 0;
        curRuleIndex = 0;
    	prevRules = true;
    }
    
    public void clearCurRules() {
    	for (Rule r: rules) {
            r.reset();
        }
    }

    public void resetRules(Block[][] newRules) {
    	clearCurRules();
        for (int i = 0; i < newRules.length && i < noRulesPerPage; i++) {
            rules[i].setRuleBlocks(newRules[i]);
        }
        for (int i = 0; i < newRules.length; i++) {
        	Rule rule = allRules.get(i+curRuleIndex);
        	rule.reset();
        	rule.setRuleBlocks(newRules[i]);
        }
    }

    private void resetInputs() {
        layer.clearInputs();
    }

    public String getRulesString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < allRules.size(); i++) {
            String ruleString = allRules.get(i).getString(i);
            if (!ruleString.equals("")) {
                res.append(ruleString);
            }
        }

        return res.toString();
    }
    
    public ArrayList<String> getRulesStringForDisplay() {
    	ArrayList<String> res = new ArrayList<String>();
    	for (int i = 0; i < noRulesPerPage; i++) {
    		String ruleString = rules[i].getStringForDisplay();
    		if (!ruleString.equals("")) {
    			res.add(ruleString);
    		}
    	}
    	//System.out.println(res.size());
    	return res;
    }
    
    // only current page of rules
    public int noOfRules() {
    	int res = 0;
    	for (int i = 0; i < noRulesPerPage; i++) {
    		if (rules[i].cellSize()>0) {
    			res++;
    		}
    	}
    	return res;
    }

    public void setupInputs(Block[] blocks, int refX, int refY) {
        resetInputs();

        BlockCoordinatesGenerator bcg = new BlockCoordinatesGenerator(refX, refY);

        for (Block b: blocks) {
        	if (b.getType() != Type.OTHER) {
        		int[] coord = bcg.getCoordinates(b.getType());
        		layer.createInput(b, coord[0], coord[1]);
        	}
        }
    }

    public void toggleLights() {
        for (Rule rule : rules) {
            rule.toggleLights();
        }
    }

    // has the ability to choose
    public void setupRules(int noRules, Block[][] newRules, boolean draggable) {
        resetRules(newRules);
        ruleLimit = noRules;
        setupRuleView(noRules, draggable);
    }

    // for read mode only
    public void setupRules(int noRules, Rule[] newRules) {
        this.rules = newRules;
        setupRuleView(noRules, false);
    }

    private void setupRuleView(int noRules, boolean draggable) {
        for (int i = 0; i < noRulesPerPage; i++) {
            if (i < noRules) {
                layer.addTargets(rules[i].getTargets());
                rules[i].displayImages(draggable);
                rules[i].lock(false);
            } else {
                rules[i].lock(true);
            }
        }
    }

    public void lightOnRule(List<Integer> ruleIndexes) {
        for(Integer ruleIndex: ruleIndexes) {
            if (ruleIndex >= 0 && ruleIndex < noRulesPerPage && curPageIndex==0) {  //on first page
            	rules[ruleIndex].lightOn();
            }
            if (ruleIndex >= rules.length && ruleIndex < 2*noRulesPerPage && curPageIndex==1) {  //on second page
            	rules[ruleIndex-noRulesPerPage].lightOn();
            }
        }
    }

    public boolean onlyConsequentUsed() {
        boolean res = true;
        for(Rule r: allRules) {
            res &= r.onlyConsequentUsed();
        }

        return res;
    }

    public LinkedList<Block> getBlockStack() {
        LinkedList<Block> blockStack = new LinkedList<>();

        for (Rule r: allRules) {
            blockStack.addAll(r.getBlockStack());
        }

        return blockStack;
    }

    public Block[][] getRules() {
        Block[][] res = new Block[allRules.size()][];

        for (int i = 0; i < res.length; i++) {
            List<Block> blocks = allRules.get(i).getBlockStack();
            res[i] = blocks.toArray(new Block[blocks.size()]);
        }

        return res;
    }
    
    ////////////////// TODO
    public Block[][] getMacroRules() {
        Block[][] res = new Block[rules.length][];

        for (int i = 0; i < res.length; i++) {
            List<Block> blocks = rules[i].getBlockStack();
            res[i] = blocks.toArray(new Block[blocks.size()]);
        }

        return res;
    }
    //////////
    
    // idManager calls
    public List<ArrayList<Block>> getRLRules() {
    	List<ArrayList<Block>> result = new ArrayList<ArrayList<Block>>(allRules.size());
    	for (int i = 0; i < allRules.size(); i++) {
    		LinkedList<Block> blocks = allRules.get(i).getBlockStack();
    		List<Block> l = new ArrayList<Block>();
    		l.addAll(blocks);
            result.add((ArrayList<Block>) l);
        }
    	return result;
    }

    public void lightOffRules() {
        for (Rule r: rules) {
            r.lightOff();
        }
    }

    public boolean mixedParadigmUsed() {
        // At least
        boolean oneOnlyConsequent = false;
        boolean oneOther = false;

        for(Rule r: allRules) {
            if (!r.isNull()) {
                boolean consequent = r.onlyConsequentUsed();
                oneOnlyConsequent |= consequent;
                oneOther |= !consequent;
            }
        }

        return oneOnlyConsequent && oneOther;
    }
    
    // ----------------------- Rule Pages --------------------- \\
    //////////////////////////////////////////////////////////////
    
    private void saveCurrRulePage() {
    	Block[][] currPage = new Block[noRulesPerPage][14];
    	
    	for(int j=0; j<rules.length; j++) {
    		List<Block> ruleBlocks = rules[j].getBlockStack();
    		for(int i=0; i<ruleBlocks.size(); i++) {
    			Block b = ruleBlocks.get(i);
    			currPage[j][i] = b;
    		}
    	}
    	
    	int size = rulePages.size();
    	if (size > 0 && curPageIndex < size) {
    		rulePages.remove(curPageIndex);
    	}
    	rulePages.add(curPageIndex, currPage);
    }
    
    public void clearRule(int index) {
    	rules[index].reset();
    }
    
    public void nextPage(Boolean playing) {
    	saveCurrRulePage();
    	curRuleIndex+=noRulesPerPage;
    	curPageIndex++;
    	prevRules = checkHelper();
    	clearCurRules();
    	loadPageRules(playing);
    }
    
    public void prevPage(Boolean playing) {
    	saveCurrRulePage();
    	curRuleIndex-=noRulesPerPage;
    	curPageIndex--;
    	prevRules = checkHelper();
    	clearCurRules();
    	loadPageRules(playing);
    }
    
    private void loadPageRules(Boolean playing) {
    	int size = rulePages.size();
    	if (!(size>0 && curPageIndex<size)) { return; }
    	
    	Block[][] page = rulePages.get(curPageIndex);
    	if (!playing) {
    		setupRules(noRulesPerPage, page, true);
    		return;
    	}
    	
    	//Write Mode: true, Read Mode: false!!
    	String mode = gameController.getCurrGameMode();
    	if (mode != "") {
    		if (mode.equals("WRITE")) {
    			setupRules(ruleLimit, page, true); 
    			/*if (ruleLimit > 0) {
    				if (ruleLimit >= noRulesPerPage) {
        			} else {
        				if (curPageIndex==1) {
        					setupRules(0, page, true);
        				} else {
        					setupRules(ruleLimit, page, true);
        				}
        			}
    			}*/
        	} else {
        		//System.out.println(mode);
        		setupRules(noRulesPerPage, page, false);
        	}
    	}
    }
    
    public void upDatePage() {
    	saveCurrRulePage();
    	int size = rulePages.size();
    	if (!(size>0 && curPageIndex<size)) { return; }
    	
    	Block[][] page = rulePages.get(curPageIndex);
    	setupRules(ruleLimit, page, true);
    }
    
    public boolean isOnwhichPage(int index) {
    	return curPageIndex==index;
    }

	public void setController(GameController gameController) {
		this.gameController = gameController;
	}
	
	public int getCurPageIndex() {
		return curPageIndex;
	}
	
	public void disableClearRuleButtons(boolean b) {
		for (Rule r:rules) {
			r.disableButton(b);
		}
	}
	
	public void showRuleUIs () {
		for (Rule r:rules) {
			r.showUIs();;
		}
	}
	
	public void disableRuleUIs(int noOfRules) {
		if (noOfRules >= noRulesPerPage) {
			showRuleUIs();
			return;
		}
		showRuleUIs();
		for(int i=noRulesPerPage-1; i>noOfRules-1; i--) {
			rules[i].disableUIs();
		}
	}
	
	public void setUpRulesForRead(Block[][] rules, boolean b) {
		Block[][] page1 = new Block[noRulesPerPage][14];
		Block[][] page2 = new Block[noRulesPerPage][14];
		int size = rulePages.size();
		int rulesSize = rules.length;
		int fPage;
		
		//First page
		if (rulesSize>=noRulesPerPage) {
			fPage = noRulesPerPage;
		} else {
			fPage = rulesSize;
		}
		
		for (int i=0; i<fPage; i++) {
			page1[i] = rules[i];
		}
		if (size > 0) { rulePages.remove(0); }
		rulePages.add(0, page1);
		
		if (rulesSize <= noRulesPerPage) {
			setupRules(fPage, rulePages.get(0), b);
			return;
		}
		
		//Second Page
		int sPage = rulesSize;
		for (int i=noRulesPerPage; i<sPage; i++) {
			page2[i-noRulesPerPage] = rules[i];
		}
		
		//resetRules(page2);
		
		if (size > 1) { rulePages.remove(1);}
		rulePages.add(1, page2);
		
		//Block[][] allPages = append(rulePages.get(0), rulePages.get(1));
		//TODO
		/*List list = new ArrayList(Arrays.asList(rulePages.get(0)));
		for (int i=0; i<size; i++) {
			allPages = ArrayUtils.addAll()
		}*/
		//allPages = ArrayUtils.addAll(rulePages.get(0), rulePages.get(1));
		
		setupRules(noRulesPerPage, rulePages.get(curPageIndex), b);
		/*for (int i = 0; i < page2.length && i < noRulesPerPage; i++) {
        	allRules.get(i+curRuleIndex).setRuleBlocks(page2[i]);
        }*/
		for (int i = 0; i < noRulesPerPage; i++) {
			allRules.get(i).setRuleBlocks(page1[i]);
			allRules.get(i+noRulesPerPage).setRuleBlocks(page2[i]);
		}
	}
	
	/*private Block[][] append(Block[][] a, Block[][] b) {
		Block[][] result = new Block[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
	}*/
    
}
