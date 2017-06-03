package com.bob.game.inputs;

public enum Type {
    FLUENT, AND, NOT, IMPLY, CONSEQUENT, DIRECTION, OTHER, Empty;

    public static boolean isValid(Type[] types) {
        Type last = null;
        boolean implied = false;
        boolean oneFluent = false;
        int nbConsequent = 0;

        for (Type type : types) {

            if (type == Empty) continue;
            if (last == null && type == AND) {
            	return false;
            }
            if (type == CONSEQUENT) nbConsequent++;
            
            if (last != null) {
                switch (last) {
                case FLUENT:
                	if (type != AND && type != IMPLY) return false;
                	oneFluent = true;
                	break;
                case AND:
                	if (type != FLUENT && type != NOT && type != DIRECTION) return false;
                	break;
                case NOT:
                	if (type != FLUENT && type != NOT && type != DIRECTION) return false;
                	break;
                case DIRECTION:
                	if (type != FLUENT) return false;
                	break;
                case IMPLY:
                	implied = true;
                	if (type != CONSEQUENT) return false;
                	break;
                case CONSEQUENT:
                	if (type != CONSEQUENT) return false;
				default:
					break;
                }
            }

            last = type;
        }

        if (last != null) {
            return (implied && last == CONSEQUENT && oneFluent && nbConsequent == 1) || (!implied && !oneFluent && last == CONSEQUENT);
        } else {
            return true;
        }
    }
}
