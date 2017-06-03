package com.bob.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelFactory {
    public static final List<Level> WRITE = new ArrayList<>();
    public static final List<Level> READ = new ArrayList<>();
    public static final List<Level> MACRO = new ArrayList<>();
    public static final List<Level> CUSTOM = new ArrayList<>();
    

    public static void initialiseLevels() {
        //populateWrite();
        //populateRead();
        //populateMacro();
    	loadLevels();
    }
    
    public static void loadLevelFiles(String dirPath, List<Level> list) {
    	FileHandle dirHandle = Gdx.files.internal(dirPath);
    	
    	for (FileHandle entry: dirHandle.list()) {
    		Level l = loadInternalLevel(entry.path());
    		l.setFileName(entry.nameWithoutExtension());
    		list.add(l);
    	}
    }
    
    private static void connectLevels(List<Level> list) {
    	for (int i = 0; i < list.size() - 1; i++) {
    		list.get(i).setNext(list.get(i+1));
        }
    }
    
    public static Level addNewCustomLevel(String fileName) {
    	Level l = LevelFactory.loadInternalLevel("levels/custom/"+fileName+".xml");
    	l.setFileName(fileName);
    	CUSTOM.add(l);
    	connectLevels(CUSTOM);
    	return l;
    }

    private static void loadLevels() {
    	loadLevelFiles("levels/write", WRITE);
    	connectLevels(WRITE);
    	
    	loadLevelFiles("levels/read", READ);
    	connectLevels(READ);
    	
    	loadLevelFiles("levels/macro", MACRO);
    	connectLevels(MACRO);
    	
    	loadLevelFiles("levels/custom", CUSTOM);
    	connectLevels(CUSTOM);
    	
        /*WRITE.add(loadInternalLevel("levels/short.xml"));
        WRITE.add(loadInternalLevel("levels/straight.xml"));
        WRITE.add(loadInternalLevel("levels/TEST.xml"));
        WRITE.add(loadInternalLevel("levels/turn.xml"));
        WRITE.add(loadInternalLevel("levels/not.xml"));
        WRITE.add(loadInternalLevel("levels/loop.xml"));*/
    }

    /*private static void populateRead() {
    	loadLevelFiles("levels/read", READ);
    	connectLevels(READ);
    	
        READ.add(loadInternalLevel("levels/guess.xml"));
        READ.add(loadInternalLevel("levels/guessBis.xml"));
        READ.add(loadInternalLevel("levels/guessTer.xml"));
        READ.add(loadInternalLevel("levels/readT.xml"));
    }*/

    /*private static void populateMacro() {
    	loadLevelFiles("levels/macro", MACRO);
    	connectLevels(MACRO);
    	
        MACRO.add(loadInternalLevel("levels/macro.xml"));
        MACRO.add(loadInternalLevel("levels/macroT.xml"));
        MACRO.add(loadInternalLevel("levels/macro2.xml"));
    }*/
    
    //load from system
    public static Level loadExternaLevel(String path) {
        return(loadLevelFromFile(new FileHandle(path)));
    }

    //load from resources folder
    public static Level loadInternalLevel(String path) {
        return(loadLevelFromFile(Gdx.files.internal(path)));
    }

    public static Level loadLevelFromFile(FileHandle file) {
        XmlReader xmlReader = new XmlReader();

        XmlReader.Element root = null;

        try {
            root = xmlReader.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String type = root.getAttribute("type");

        switch (type) {
            case "WRITE":
                return new WriteLevel(root);
            case "READ":
                return new ReadLevel(root);
            case "MACRO":
                return new MacroLevel(root);
            default:
                return null;
        }
    }
    
}
