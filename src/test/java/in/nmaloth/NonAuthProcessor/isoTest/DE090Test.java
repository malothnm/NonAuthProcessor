package in.nmaloth.NonAuthProcessor.isoTest;

import com.google.common.primitives.Bytes;
import com.nithin.iso8583.iso.config.LoadingConfig;
import com.nithin.iso8583.iso.elementdef.common.constants.ElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.EncodingType;
import com.nithin.iso8583.iso.elementdef.common.constants.ISOElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.PaddingType;
import com.nithin.iso8583.iso.elementdef.common.interfaces.IntElementLengthValue;
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
import jdk.jshell.execution.Util;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DE090Test {

    private ISOProtoElement isoProtoElement;

    public DE090Test(){
        LoadingConfig loadingConfig = new LoadingConfig();
        isoProtoElement = loadingConfig.createProtoElement(90, "/isomessage");
    }


    @Test
    void getId() {
        assertEquals("DE090", isoProtoElement.getId());
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
        assertEquals(21,isoProtoElement.getMaxLength());
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
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE090S001");
        assertAll("SubElement Tests",
                ()->assertEquals("DE090",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.INT,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.BCD,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(2,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(0,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(2,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }

    @Test
    void subElementTest2(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE090S002");
        assertAll("SubElement Tests",
                ()->assertEquals("DE090",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.INT,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.BCD,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(3,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(2,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(5,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }

    @Test
    void subElementTest3(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE090S003");
        assertAll("SubElement Tests",
                ()->assertEquals("DE090",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.HEX,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(5,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(5,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(10,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }

    @Test
    void subElementTest4(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement.getIsoProtoSubElementMap().getSubElement("DE090S004");
        assertAll("SubElement Tests",
                ()->assertEquals("DE090",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.HEX,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(11,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(10,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(21,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }



    @Test
    void  subElementLengthValue() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalMti = new byte[]{0x01,0x00};
        byte[] originalTraceNumber = new byte[]{0x12,0x34,0x56};
        byte[] originalTransmissionDate = new byte[]{0x12,0x22,0x15,0x33,0x22};
        byte[] originalAcquirerIdAndFwdInstId = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,
        0x12,0x34,0x56,0x78,(byte) 0x90,0x12};
        byte[] de090 = Bytes.concat(originalMti,originalTraceNumber,originalTransmissionDate,originalAcquirerIdAndFwdInstId);


        basicElement.setElementValue(de090);
        basicElement.setElementLength(new byte[]{0x15});

        Element element = isoProtoElement.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement1 = subElementMap.get("DE090S001");
        SubElement subElement2 = subElementMap.get("DE090S002");
        SubElement subElement3 = subElementMap.get("DE090S003");
        SubElement subElement4 = subElementMap.get("DE090S004");




        assertAll("SubElement 1 ",
                ()->assertEquals("DE090S001",subElement1.getId()),
                ()->assertEquals(100,((IntElementLengthValue)subElement1.getElementLengthValue()).getValue()),
                ()->assertEquals(2,subElement1.getElementLengthValue().getLength()),
                ()->assertEquals("DE090S002",subElement2.getId()),
                ()->assertEquals(123456,((IntElementLengthValue)subElement2.getElementLengthValue()).getValue()),
                ()->assertEquals(3,subElement2.getElementLengthValue().getLength()),
                ()->assertEquals("DE090S003",subElement3.getId()),
                ()->assertEquals("1222153322",((StringElementLengthValue)subElement3.getElementLengthValue()).getValue()),
                ()->assertEquals(5,subElement3.getElementLengthValue().getLength()),
                ()->assertEquals("DE090S004",subElement4.getId()),
                ()->assertEquals("1234567890123456789012",((StringElementLengthValue)subElement4.getElementLengthValue()).getValue()),
                ()->assertEquals(11,subElement4.getElementLengthValue().getLength())


                );

    }

    @Test
    void  subElementLengthValue1() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalMti = new byte[]{0x01,0x00};
        byte[] originalTraceNumber = new byte[]{0x12,0x34,0x56};
        byte[] originalTransmissionDate = new byte[]{0x12,0x22,0x15,0x33,0x22};
        byte[] originalAcquirerIdAndFwdInstId = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,
                0x12,0x34,0x56,0x78,(byte) 0x90,0x12};
        byte[] de090 = Bytes.concat(originalMti,originalTraceNumber,originalTransmissionDate,originalAcquirerIdAndFwdInstId);


        basicElement.setElementValue(de090);

        Element element = isoProtoElement.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement6 = subElementMap.get("DE127S005");
        assertNull(subElement6);
        assertEquals(4,subElementMap.size());

    }

    @Test
    void convertToBytes() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] originalMti = new byte[]{0x01,0x00};
        byte[] originalTraceNumber = new byte[]{0x12,0x34,0x56};
        byte[] originalTransmissionDate = new byte[]{0x12,0x22,0x15,0x33,0x22};
        byte[] originalAcquirerIdAndFwdInstId = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,
                0x12,0x34,0x56,0x78,(byte) 0x90,0x12};
        byte[] de090 = Bytes.concat(originalMti,originalTraceNumber,originalTransmissionDate,originalAcquirerIdAndFwdInstId);

        basicElement.setElementValue(de090);

        Element element = isoProtoElement.createNewISOElement(basicElement);

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);
        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }

    @Test
    void createDE003 () throws Exception {
        SubElement subElement01 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE090S001").createNewISOSubElement(100);
        SubElement subElement02 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE090S002").createNewISOSubElement(123456);

        SubElement subElement03 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE090S003").createNewISOSubElement("1222153322");
        SubElement subElement04 = isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("DE090S004").createNewISOSubElement("1234567890123456789012");

        List<SubElement> subElementList = new ArrayList<>();
        subElementList.add(subElement01);
        subElementList.add(subElement02);
        subElementList.add(subElement03);
        subElementList.add(subElement04);

        SubElementMap subElementMap = isoProtoElement.getIsoProtoSubElementMap().createNewSubElementMap(subElementList);
        Element element = isoProtoElement.createNewISOElement(subElementMap);

        BasicElement basicElement= new BasicElement();
        byte[] originalMti = new byte[]{0x01,0x00};
        byte[] originalTraceNumber = new byte[]{0x12,0x34,0x56};
        byte[] originalTransmissionDate = new byte[]{0x12,0x22,0x15,0x33,0x22};
        byte[] originalAcquirerIdAndFwdInstId = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,
                0x12,0x34,0x56,0x78,(byte) 0x90,0x12};
        byte[] de090 = Bytes.concat(originalMti,originalTraceNumber,originalTransmissionDate,originalAcquirerIdAndFwdInstId);


        basicElement.setElementValue(de090);

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);
        System.out.println(Utils.hexToString(basicElement.getElementValue()));
        System.out.println(Utils.hexToString(basicElement1.getElementValue()));
        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }


}
