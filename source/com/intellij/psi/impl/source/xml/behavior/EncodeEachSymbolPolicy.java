package com.intellij.psi.impl.source.xml.behavior;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.DummyHolder;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.CharTable;

public class EncodeEachSymbolPolicy extends DefaultXmlPsiPolicy{
  public ASTNode encodeXmlTextContents(String displayText) {
    if(!toCode(displayText)) return super.encodeXmlTextContents(displayText);
    final FileElement dummyParent = new DummyHolder(null, null).getTreeElement();
    int sectionStartOffset = 0;
    int offset = 0;
    while (offset < displayText.length()) {
      if (toCode(displayText.charAt(offset))) {
        final String plainSection = displayText.substring(sectionStartOffset, offset);
        if (plainSection.length() > 0) {
          TreeUtil.addChildren(dummyParent, (TreeElement)super.encodeXmlTextContents(plainSection));
        }
        TreeUtil.addChildren(dummyParent, createCharEntity(displayText.charAt(offset), dummyParent.getCharTable()));
      }
    }

    return dummyParent.getFirstChildNode();
  }

  private static TreeElement createCharEntity(char ch, CharTable charTable) {
    switch (ch) {
      case '<':
        return Factory.createLeafElement(
          XmlTokenType.XML_CHAR_ENTITY_REF,
          "&lt;".toCharArray(),
          0, 4, -1,
          charTable);
      case '>':
        return Factory.createLeafElement(
          XmlTokenType.XML_CHAR_ENTITY_REF,
          "&gt;".toCharArray(),
          0, 4, -1,
          charTable);
      case '&':
        return Factory.createLeafElement(
          XmlTokenType.XML_CHAR_ENTITY_REF,
          "&amp;".toCharArray(),
          0, 5, -1,
          charTable);
      case '\u00a0':
        return Factory.createLeafElement(
          XmlTokenType.XML_CHAR_ENTITY_REF,
          "&nbsp;".toCharArray(),
          0, 6, -1,
          charTable);

      default:
        final String charEncoding = "&#" + (int)ch + ";";
        return Factory.createLeafElement(
          XmlTokenType.XML_CHAR_ENTITY_REF,
          charEncoding.toCharArray(),
          0, charEncoding.length(), -1,
          charTable);
    }
  }
}
