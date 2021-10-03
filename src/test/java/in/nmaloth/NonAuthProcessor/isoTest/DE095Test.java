package in.nmaloth.NonAuthProcessor.isoTest;

import com.google.common.primitives.Bytes;
import com.nithin.iso8583.iso.config.LoadingConfig;
import com.nithin.iso8583.iso.elementdef.common.constants.ElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.EncodingType;
import com.nithin.iso8583.iso.elementdef.common.constants.ISOElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.PaddingType;
import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.LongElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.StringElementLengthValue;
import com.nithin.iso8583.iso.elementdef.common.interfaces.SubElementLengthValue;
import com.nithin.iso8583.iso.elementdef.element.BasicElement;
import com.nithin.iso8583.iso.elementdef.element.Element;
import com.nithin.iso8583.iso.elementdef.element.ISOProtoElement;
import com.nithin.iso8583.iso.elementdef.subelement.SubElementType;
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.fixed.ISOProtoSubElementFixed;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DE095Test {

    private ISOProtoElement isoProtoElement;

    public DE095Test(){
        LoadingConfig loadingConfig = new LoadingConfig();
        isoProtoElement = loadingConfig.createProtoElement(95, "/isomessage");
    }


    @Test
    void getId() {
        assertEquals("DE095", isoProtoElement.getId());
    }

    @Test
    void getElementType() {
        assertEquals(ElementType.FIXED, isoProtoElement.getElementType());
    }

    @Test
    void getIsoTargetType() {
        assertEquals(ISOElementType.SUB_ELEMENT, isoProtoElement.getIsoTargetType());
    }

    @Test
    void getIsoSourceType() {
        assertEquals(EncodingType.SUB_ELEMENT,isoProtoElement.getIsoSourceType());
    }

    @Test
    void getLengthOfLength() {
        assertEquals(0,isoProtoElement.getLengthOfLength());
    }

    @Test
    void getMaxLength() {
        assertEquals(42,isoProtoElement.getMaxLength());
    }

    @Test
    void isUsagePresent() {
        assertFalse(isoProtoElement.isUsagePresent());
    }

    @Test
    void isToBeEncrypted() {
        assertFalse(isoProtoElement.isToBeEncrypted());
    }

    @Test
    void getPaddingType() {
        assertEquals(PaddingType.NONE,isoProtoElement.getPaddingType());
    }

    @Test
    void subElementTest1(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE095S001");
        assertAll("SubElement Tests",
                ()->assertEquals("DE095",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.LONG,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(12,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(0,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(12,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }

    @Test
    void subElementTest2(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE095S002");
        assertAll("SubElement Tests",
                ()->assertEquals("DE095",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(30,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(12,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(42,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }



    @Test
    void  subElementLengthValue() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalTransactionAmount = "123456789012".getBytes(StandardCharsets.UTF_8);
        byte[] otherFields = "123456789012345678901234567890".getBytes(StandardCharsets.UTF_8);

        byte[] de095 = Bytes.concat(originalTransactionAmount,otherFields);


        basicElement.setElementValue(de095);
//        basicElement.setElementLength(new byte[]{0x15});

        Element element = isoProtoElement.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement1 = subElementMap.get("DE095S001");
        SubElement subElement2 = subElementMap.get("DE095S002");




        assertAll("SubElement 1 ",
                ()->assertEquals("DE095S001",subElement1.getId()),
                ()->assertEquals(123456789012L,((LongElementLengthValue)subElement1.getElementLengthValue()).getValue()),
                ()->assertEquals(12,subElement1.getElementLengthValue().getLength()),
                ()->assertEquals("DE095S002",subElement2.getId()),
                ()->assertEquals("123456789012345678901234567890",((StringElementLengthValue)subElement2.getElementLengthValue()).getValue())


                );

    }

    @Test
    void  subElementLengthValue1() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalTransactionAmount = "123456789012".getBytes(StandardCharsets.UTF_8);
        byte[] otherFields = "123456789012345678901234567890".getBytes(StandardCharsets.UTF_8);

        byte[] de095 = Bytes.concat(originalTransactionAmount,otherFields);

        basicElement.setElementValue(de095);

        Element element = isoProtoElement.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement6 = subElementMap.get("DE095S003");
        assertNull(subElement6);
        assertEquals(2,subElementMap.size());

    }

    @Test
    void convertToBytes() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalTransactionAmount = "123456789012".getBytes(StandardCharsets.UTF_8);
        byte[] otherFields = "123456789012345678901234567890".getBytes(StandardCharsets.UTF_8);

        byte[] de095 = Bytes.concat(originalTransactionAmount,otherFields);
        basicElement.setElementValue(de095);

        Element element = isoProtoElement.createNewISOElement(basicElement);

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);
        System.out.println(Utils.hexToString(basicElement1.getElementValue()));
        System.out.println(Utils.hexToString(basicElement.getElementValue()));

        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }

    @Test
    void createDE003 () throws Exception {
        SubElement subElement01 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE095S001").createNewISOSubElement(123456789012L);
        SubElement subElement02 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE095S002").createNewISOSubElement("123456789012345678901234567890");


        List<SubElement> subElementList = new ArrayList<>();
        subElementList.add(subElement01);
        subElementList.add(subElement02);

        SubElementMap subElementMap = isoProtoElement.getIsoProtoSubElementMap().createNewSubElementMap(subElementList);
        Element element = isoProtoElement.createNewISOElement(subElementMap);

        BasicElement basicElement= new BasicElement();
        byte[] originalTransactionAmount = "123456789012".getBytes(StandardCharsets.UTF_8);
        byte[] otherFields = "123456789012345678901234567890".getBytes(StandardCharsets.UTF_8);

        byte[] de095 = Bytes.concat(originalTransactionAmount,otherFields);

        basicElement.setElementValue(de095);

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);
        System.out.println(Utils.hexToString(basicElement.getElementValue()));
        System.out.println(Utils.hexToString(basicElement1.getElementValue()));
        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }


}
