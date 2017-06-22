package com.bob.game.inputs;

public enum Block {
	
    // Color tiles
    WHITE("white(X,Y)", "white", Type.FLUENT, "If Bob is on a white cell"),
    RED("red(X,Y)", "red", Type.FLUENT, "If Bob is on a red cell"),
    YELLOW("yellow(X,Y)", "yellow", Type.FLUENT, "If Bob is on a yellow cell"),
    GREEN("green(X,Y)", "green", Type.FLUENT, "If Bob is on a green cell"),
    ORANGE("orange(X,Y)", "orange", Type.FLUENT, "If Bob is on a orange cell"),
    PURPLE("purple(X,Y)", "purple", Type.FLUENT, "If Bob is on a purple cell"),
    OCEAN("water(X,Y)", "ocean", Type.FLUENT, "If Bob is on a ocean cell"),
    BOAT("boat(X,Y)", "boat", Type.FLUENT, "If Bob is on a boat"),
    
    // Other tiles
    GOLD("gold(X,Y)", "gold", Type.OTHER, "If Bob is on a gold cell"),
    QUESTION("question(X,Y)", "question", Type.OTHER, "If Bob is on a question cell"),
    BULB("bulb(X,Y)", "bulb", Type.OTHER, "Add a light bulb"),
    NOBULB("nobulb(X,Y)", "nobulb", Type.OTHER, "Delete a light bulb"),
    BOB("bob(X,Y)", "bob", Type.OTHER, "This is bob"),
    NOBOAT("noboat(X,Y)", "noboat", Type.OTHER, "Delete a boat"),

    // Direction Tiles
    NEXT_TILE("frontIs", "next_tile", Type.DIRECTION, 
    		"Place it before a color to check whether the next cell of Bob is of that color or not"),
    PREV_TILE("prevWas", "prev_tile", Type.DIRECTION, 
    		"Place it before a color to check whether the previous cell of Bob is of that color or not"),
    LEFT_TILE("leftIs", "left_tile", Type.DIRECTION, 
    		"Place it before a color to check whether the left cell of Bob is of that color or not"),
    RIGHT_TILE("rightIs", "right_tile", Type.DIRECTION, 
    		"Place it before a color to check whether the right cell of Bob is of that color or not"),
    
    // Movements Instructions
    WEST("moveWest", "moveWest", Type.CONSEQUENT, "Bob should go West"), //LEFT
    NORTH("moveNorth", "moveNorth", Type.CONSEQUENT, "Bob should go North"),  //UP
    SOUTH("moveSouth", "moveSouth", Type.CONSEQUENT, "Bob should go South"), //DOWN
    EAST("moveEast", "moveEast", Type.CONSEQUENT, "Bob should go East"), //RIGHT

    // Other instructions
    WAIT("wait", "pause", Type.CONSEQUENT, "Bob should wait"),

    // Connectors
    AND("&", "and", Type.AND, "AND, to be used in: if a AND b"),
    NOT("!", "not", Type.NOT, "NOT, to be used in: NOT a"),
	IMPLY("->", "imply", Type.IMPLY, "IMPLY/THEN, to be used in: if a THEN b");
	
    private final String LPSString;
    private final String imageName;
    private final Type type;
    private final String tooltip;

    Block(String lps, String image, Type type, String tooltip) {
        this.LPSString = lps;
        this.imageName = image;
        this.type = type;
        this.tooltip = tooltip;
    }

    public String getLPSString() {
        return LPSString;
    }

    public String getImageName() {
        return imageName;
    }

    public Type getType() {
        return type;
    }

    public String getTooltip() {
        return tooltip;
    }

    public static Block getBlock(String name) {
        for (Block b: Block.values()) {
            if (b.getImageName().equals(name)) return b;
        }
        return null;
    }
}
