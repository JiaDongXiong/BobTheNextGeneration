package com.bob.game.inputs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bob.main.TextureFactory;

import java.util.LinkedList;

public class Rule {

    private Image light;
    private Image lock1;
    private Image lock2;
    private Image arrow;
    private TextButton button;
    private final RuleCell[] cells;
    private Drawable greenLight;
    private Drawable redLight;

    public Rule() {
        cells = new RuleCell[14];
        for (int i=0; i < cells.length; ++i) {
            cells[i] = new RuleCell();
        }
    }

    public void initView(InputsLayer layer, Skin skin, int startingX, int startingY){
        //Cells
        for (int i=0; i < cells.length; ++i) {
            Image bkgImage = new Image(skin, "target");
            if (i<7) {
            	bkgImage.setBounds(startingX + i * 60, startingY, 50, 50);
            	cells[i].initView(layer, startingX + i * 60, startingY, bkgImage, skin);
            } else {
            	bkgImage.setBounds(startingX + (i-7) * 60, startingY-70, 50, 50);
            	cells[i].initView(layer, startingX + (i-7) * 60, startingY-70, bkgImage, skin);
            }
        }

        // Red light, green light
        greenLight = skin.getDrawable("green_light");
        redLight = skin.getDrawable("red_light");

        light = new Image(greenLight);
        light.setBounds(startingX - 25, startingY, light.getWidth(), light.getHeight());
        layer.addActor(light);

        // Locking pane
        lock1 = new Image(TextureFactory.createTexture("blocks/locked.png"));
        lock1.setBounds(startingX - 75, startingY - 12, 500, 70);
        lock1.setVisible(false);
        layer.addActor(lock1);
        lock2 = new Image(TextureFactory.createTexture("blocks/locked.png"));
        lock2.setBounds(startingX - 75, startingY - 12-70, 500, 70);
        lock2.setVisible(false);
        layer.addActor(lock2);

        // Arrow for current rule
        arrow = new Image(TextureFactory.createTexture("lights/arrow.png"));
        arrow.setBounds(startingX - 100, startingY - 2, 60, 45);
        arrow.setVisible(false);
        layer.addActor(arrow);
    }

    public DragAndDrop.Target[] getTargets() {
        DragAndDrop.Target[] targets = new DragAndDrop.Target[cells.length];

        for(int i = 0; i < cells.length; ++i) {
            targets[i] = cells[i].getTarget();
        }

        return targets;
    }
    
    public int cellSize() {
    	return arrayToList(cells).size();
    }
    
    public String getStringForDisplay() {
    	//StringBuilder sb = new StringBuilder("isIn(X,Y) & wasIn(U,V) & nextIs(A, B) & leftIs(C, D) & rightIs(E, F) & ");
    	StringBuilder sb = new StringBuilder();
    	boolean notEmpty = false;
    	
    	LinkedList<String> cellList = arrayToList(cells);
    	if(cellList.size() > 0) {
    		notEmpty = true;
    	}
    	
    	String listString = displayHelper(cellList);
    	sb.append(listString);
    	
    	return notEmpty ? sb.toString() : "";
    }
    
    private String displayHelper(LinkedList<String> cellList) {
    	StringBuilder sb = new StringBuilder();
    	int size = cellList.size();
    	for(int i = 0; i < size; i++) {
    		sb.append(cellList.get(i));
    		if (i != size-1) {
    			sb.append(" ");
    		}
    	}
    	return sb.toString();
    }

    public String getString(int ruleIndex) {
        return getString(ruleIndex, 0);
    }

    public String getString(int ruleIndex, int noLights) {

        StringBuilder sb = new StringBuilder("\tisIn(X,Y) & wasIn(U,V) & nextIs(A, B) & leftIs(C, D) & rightIs(E, F) "
        		+ "& lights(" + noLights + ") & !lightBulb(X,Y) & ");
        boolean notEmpty = false;
        
        LinkedList<String> cellList = arrayToList(cells);
        String lastCell = "";
        if(cellList.size() > 0) {
        	lastCell = cellList.getLast();
        	notEmpty = true;
        }
        
        checkLPSStrings(cellList);
        
        String listString = cellListToString(cellList);
        String[] strings = listString.split(" -> ");
        String leftS = sb.toString()+strings[0]+" -> ";
        
        sb.append(listString);
        
        sb.append("("+ruleIndex+").\n");
        //duplicate rules for updating bob's direction
        switch (lastCell) {
        case "moveSouth":
        	sb.append(leftS+"stepOnBoatSouth().\n");
        	sb.append(leftS+"triggerUpdateNextIsS().\n");
        	sb.append(leftS+"triggerUpdateLeftIsS().\n");
        	sb.append(leftS+"triggerUpdateRightIsS().\n");
        	break;
        case "moveNorth":
        	sb.append(leftS+"stepOnBoatNorth().\n");
        	sb.append(leftS+"triggerUpdateNextIsN().\n");
        	sb.append(leftS+"triggerUpdateLeftIsN().\n");
        	sb.append(leftS+"triggerUpdateRightIsN().\n");
        	break;
        case "moveEast":
        	sb.append(leftS+"stepOnBoatEast().\n");
        	sb.append(leftS+"triggerUpdateNextIsE().\n");
        	sb.append(leftS+"triggerUpdateLeftIsE().\n");
        	sb.append(leftS+"triggerUpdateRightIsE().\n");
        	break;
        case "moveWest":
        	sb.append(leftS+"stepOnBoatWest().\n");
        	sb.append(leftS+"triggerUpdateNextIsW().\n");
        	sb.append(leftS+"triggerUpdateLeftIsW().\n");
        	sb.append(leftS+"triggerUpdateRightIsW().\n");
        	break;
        default:
        }

        return notEmpty ? sb.toString() : "";
    }
    
    private String cellListToString(LinkedList<String> cellList) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < cellList.size(); i++) {
    		sb.append(cellList.get(i));
    	}
    	return sb.toString();
    }
    
    private LinkedList<String> arrayToList(RuleCell[] array) {
    	LinkedList <String> res = new LinkedList<String>();
    	
    	for (int i = 0; i < array.length; i++) {
    		String lpsString = array[i].getLPSString();
    		if (!lpsString.isEmpty()) {
    	    	res.add(lpsString);
    	    }
    	}
    	return res;
    }
    
    private void checkLPSStrings(LinkedList<String> list) {
    	String last = null;
    	int curIndex = 0;
    	while (curIndex < list.size()) {
    		String cur = list.get(curIndex);
    		
    		if (last != null) {
    			if (last.equals(Block.NOT.getLPSString())) { // equals NOT
    				if (cur.equals("!")) {
    					int lastIndex = curIndex-1;
    					list.remove(curIndex);
    					list.remove(lastIndex);
    					if (lastIndex-1 >= 0) {
        					last = list.get(lastIndex-1);
    					} else {
    						last = null;
    					}
    					curIndex = lastIndex;
    					continue;
    				}
    			}
    			
    			if (last.equals(Block.NEXT_TILE.getLPSString())) { //equals Next_Tile
    				String color = isColors(cur);
    				if (color != "") {
    					int lastIndex = curIndex-1;
    					list.remove(curIndex);
    					list.remove(lastIndex);
    					list.add(lastIndex, color+"(A,B)");
    					last = color+"(A,B)";
    					continue;
    				}
    			}
    			
    			if (last.equals(Block.PREV_TILE.getLPSString())) { //equals prev_Tile
    				String color = isColors(cur);
    				if (color != "") {
    					int lastIndex = curIndex-1;
    					list.remove(curIndex);
    					list.remove(lastIndex);
    					list.add(lastIndex, color+"(U,V)");
    					last = color+"(U,V)";
    					continue;
    				}
    			}
    			
    			if (last.equals(Block.LEFT_TILE.getLPSString())) { //equals left_Tile
    				String color = isColors(cur);
    				if (color != "") {
    					int lastIndex = curIndex-1;
    					list.remove(curIndex);
    					list.remove(lastIndex);
    					list.add(lastIndex, color+"(C,D)");
    					last = color+"(C,D)";
    					continue;
    				}
    			}
    			
    			if (last.equals(Block.RIGHT_TILE.getLPSString())) { //equals right_Tile
    				String color = isColors(cur);
    				if (color != "") {
    					int lastIndex = curIndex-1;
    					list.remove(curIndex);
    					list.remove(lastIndex);
    					list.add(lastIndex, color+"(E,F)");
    					last = color+"(E,F)";
    					continue;
    				}
    			}
    			
    		}
    		
    		last = cur;
    		curIndex++;
    	}
    }
    
    private String isColors(String s) {
    	switch (s) {
    	case "white(X,Y)":
    		return "white";
    	case "red(X,Y)":
    		return "red";
    	case "yellow(X,Y)":
    		return "yellow";
    	case "orange(X,Y)":
    		return "orange";
    	case "purple(X,Y)":
    		return "purple";
    	case "green(X,Y)":
    		return "green";
    	case "water(X,Y)":
    		return "water";
    	case "boat(X,Y)":
    		return "boat";
    	default:
    		return "";
    	}
    }
    
    public boolean isValid() {
        Type[] types = new Type[cells.length];

        for (int i = 0; i < cells.length; i++) {
        	Type t = cells[i].getType();
        	if(t != null) {
        		types[i] = t;
        	} else {
        		types[i]= Type.Empty;
        	}
        }

        return Type.isValid(types);
    }

    public void reset() {
        for (RuleCell cell : cells) {
            cell.reset();
        }
    }
    
    public void setRuleBlocks(Block[] newRule) {
        for (int i = 0; i < newRule.length && i < cells.length; ++i) {
            cells[i].setPayload(newRule[i]);
        }
    }
    
    public void setRuleBlocks(LinkedList<Block> newRule) {
        for (int i = 0; i < newRule.size() && i < cells.length; ++i) {
            cells[i].setPayload(newRule.get(i));
        }
    }

    public void toggleLights() { //Duplication of verification, could be cached if needed
        light.setDrawable(isValid() ? greenLight : redLight);
    }

    public void lock(boolean isLocked) {
    	lock2.setVisible(isLocked);
    	lock1.setVisible(isLocked);
    }

    public void displayImages(boolean draggable) {
        for (RuleCell c: cells) {
            c.setImage(draggable);
        }
    }

    public void lightOn() {
        arrow.setVisible(true);
    }

    public void lightOff() {
        arrow.setVisible(false);
    }
    
    public void disableButton(boolean b) {
    	button.setDisabled(b);
    }
    
    public void disableUIs() {
    	light.setVisible(false);
    	arrow.setVisible(false);
    	button.setVisible(false);
    }
    
    public void showUIs() {
    	light.setVisible(true);
    	arrow.setVisible(true);
    	button.setVisible(true);
    }
    

    public boolean onlyConsequentUsed() {
        boolean res = true;
        for(RuleCell c: cells) {
            res &= (c.getType() == null) || (c.getType() == Type.CONSEQUENT);
        }

        return res;
    }

    public boolean isNull() {
        boolean res = true;
        for(RuleCell c: cells) {
            res &= (c.getType() == null);
        }

        return res;
    }

    public LinkedList<Block> getBlockStack() {
        LinkedList<Block> blockStack = new LinkedList<>();

        for (RuleCell c : cells) {
            Block b = c.getBlock();
            if (b != null) {
            	//System.out.println(b.getImageName());
                blockStack.add(b);
            }
        }

        return blockStack;
    }
    
    public void addClearButton(InputsLayer layer, Skin skin, int startingX, int startingY, final InputsManager manager) {
    	button = new TextButton("X", skin, "yellow_square_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
            }
        };
        button.setBounds(startingX-66, startingY-69, 58, 53);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	if(!button.isDisabled()) {
            		reset();
            		manager.upDatePage();
            	}
            }
        });
        layer.addActor(button);
    }

}
