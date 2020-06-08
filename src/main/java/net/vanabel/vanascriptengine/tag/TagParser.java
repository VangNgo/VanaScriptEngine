package net.vanabel.vanascriptengine.tag;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.tag.attribute.Attribute;
import net.vanabel.vanascriptengine.tag.base.AbstractTagBase;
import net.vanabel.vanascriptengine.util.DuoNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class TagParser {

    public static AbstractTagBase getTagBaseFromAttributeString(String str) {
        return parseTagString(str).getLeft();
    }

    public static Attribute.Component[] getComponentsFromAttributeString(String str) {
        return parseTagString(str).getRight();
    }



    private final static Map<String, DuoNode<AbstractTagBase, Attribute.Component[]>> COMPONENTS_CACHE = new HashMap<>();

    private static void throwIllegalArgumentForSyntax(String atr, int index, String msg) {
        throw new IllegalArgumentException("Invalid syntax in the attribute string \"" + atr + "\" at index " + index +
                ": " + msg);
    }

    private static DuoNode<AbstractTagBase, Attribute.Component[]> parseTagString(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Attribute string cannot be empty or null!");
        }
        DuoNode<AbstractTagBase, Attribute.Component[]> node = COMPONENTS_CACHE.get(str);
        if (node != null) {
            return node;
        }

        AbstractTagBase finalBase = null;
        ArrayList<Attribute.Component> compList = new ArrayList<>(64);
        int start = 0, end = -1, parens = 0;
        String name = null, context = null;
        boolean processedTagBase = false;
        boolean quoted = false, isDoubleQuote = false, hadContext = false;
        Map<String, AbstractObject> cValFull = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            switch (c) {
                case '\\':
                    if (!quoted && parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Illegal escape character found.");
                    }
                    break;
                case '<':
                case '>':
                    if (!quoted && parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray tag mark found.");
                    }
                    break;
                case '(':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        if (hadContext) {
                            throwIllegalArgumentForSyntax(str, i, "Malformed attribute context.");
                        }
                        name = str.substring(start, i);
                        hadContext = true;
                    }
                    parens++;
                    break;
                case ')':
                    if (quoted) {
                        continue;
                    }
                    if (parens <= 0) {
                        throwIllegalArgumentForSyntax(str, i, "Imbalanced parentheses.");
                    }
                    parens--;
                    break;
                case '=':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray equals symbol [=] found.");
                    }
                    // TODO: This
                    break;
                case ';':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray semicolon [;] found.");
                    }
                    // TODO: This
                    break;
                case '"':
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray quote [\"] found.");
                    }
                    if (!quoted) {
                        quoted = true;
                        isDoubleQuote = true;
                    }
                    else if (isDoubleQuote) {
                        quoted = false;
                    }
                    break;
                case '\'':
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray quote ['] found.");
                    }
                    if (!quoted) {
                        quoted = true;
                        isDoubleQuote = false;
                    }
                    else if (!isDoubleQuote) {
                        quoted = false;
                    }
                    break;
                case '.':
                    if (quoted || parens > 0) {
                        continue;
                    }
                    hadContext = false;
                    if (!processedTagBase) {
                        processedTagBase = true;
                        finalBase = new AbstractTagBase("nothing", "nothing") {}; // TODO: Replace this with a legitimate base
                        break;
                    }
                    // TODO: This
                    break;
            }
        }

        if (finalBase == null) {
            // TODO: This?
        }

        compList.trimToSize();
        node = new DuoNode<>(finalBase, compList.toArray(new Attribute.Component[0]));
        COMPONENTS_CACHE.put(str, node);
        return node;
    }
}
