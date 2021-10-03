package in.nmaloth.NonAuthProcessor.isoTest;

import com.nithin.iso8583.iso.config.LoadingConfig;
import com.nithin.iso8583.iso.elementdef.common.constants.ElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.EncodingType;
import com.nithin.iso8583.iso.elementdef.common.constants.ISOElementType;
import com.nithin.iso8583.iso.elementdef.common.constants.PaddingType;
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
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ISOFileMaintenanceTest {


    LoadingConfig loadingConfig ;
    private ISOProtoElement isoProtoElement127Ex;

    public ISOFileMaintenanceTest(){

        loadingConfig = new LoadingConfig();
        isoProtoElement127Ex = loadingConfig.createProtoElement(127, "/isomessage/DE127/exceptionFile");
    }



    @Test
    void getId() {
        assertEquals("DE127", isoProtoElement127Ex.getId());
    }

    @Test
    void getElementType() {
        assertEquals(ElementType.FIXED, isoProtoElement127Ex.getElementType());
    }

    @Test
    void getIsoTargetType() {
        assertEquals(ISOElementType.SUB_ELEMENT, isoProtoElement127Ex.getIsoTargetType());
    }

    @Test
    void getIsoSourceType() {
        assertEquals(EncodingType.SUB_ELEMENT,isoProtoElement127Ex.getIsoSourceType());
    }

    @Test
    void getLengthOfLength() {
        assertEquals(0,isoProtoElement127Ex.getLengthOfLength());
    }

    @Test
    void getMaxLength() {
        assertEquals(11,isoProtoElement127Ex.getMaxLength());
    }

    @Test
    void isUsagePresent() {
        assertTrue(isoProtoElement127Ex.isUsagePresent());
    }

    @Test
    void isToBeEncrypted() {
        assertFalse(isoProtoElement127Ex.isToBeEncrypted());
    }

    @Test
    void getPaddingType() {
        assertEquals(PaddingType.NONE,isoProtoElement127Ex.getPaddingType());
    }

    @Test
    void subElementTest1(){
        ISOProtoSubElement isoProtoSubElement = isoProtoElement127Ex.getIsoProtoSubElementMap().getSubElement("DE127S001");
        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElement.getIsoSourceType()),
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
        ISOProtoSubElement isoProtoSubElement = isoProtoElement127Ex.getIsoProtoSubElementMap().getSubElement("DE127S002");
        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElement.getParentId()),
                ()->assertEquals(SubElementType.FIXED,isoProtoSubElement.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(9,isoProtoSubElement.getMaxLength()),
                ()->assertFalse(isoProtoSubElement.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElement.getElementType()),
                ()->assertEquals(0,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(2,((ISOProtoSubElementFixed)isoProtoSubElement).getStartingOffset()),
                ()->assertEquals(11,((ISOProtoSubElementFixed)isoProtoSubElement).getEndingOffset())

        );
    }



    @Test
    void  subElementLengthValue() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] bytes = "01A        ".getBytes(StandardCharsets.UTF_8);

        basicElement.setElementValue(bytes);
//        basicElement.setElementLength(new byte[]{0x00});

        Element element = isoProtoElement127Ex.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement1 = subElementMap.get("DE127S001");
        SubElement subElement2 = subElementMap.get("DE127S002");


        assertAll("SubElement 1 ",
                ()->assertEquals("DE127S001",subElement1.getId()),
                ()->assertEquals("01",((StringElementLengthValue)subElement1.getElementLengthValue()).getValue()),
                ()->assertEquals(2,subElement1.getElementLengthValue().getLength()),
                ()->assertEquals("DE127S002",subElement2.getId()),
                ()->assertEquals("A        ",((StringElementLengthValue)subElement2.getElementLengthValue()).getValue()),
                ()->assertEquals(9,subElement2.getElementLengthValue().getLength())


        );

    }

    @Test
    void  subElementLengthValue1() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] bytes = "01A        ".getBytes(StandardCharsets.UTF_8);


        basicElement.setElementValue(bytes);
//        basicElement.setElementLength(new byte[]{0x00});

        Element element = isoProtoElement127Ex.createNewISOElement(basicElement);

        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();

        SubElement subElement6 = subElementMap.get("DE127S003");
        assertNull(subElement6);
        assertEquals(2,subElementMap.size());

    }

    @Test
    void convertToBytes() throws Exception {

        BasicElement basicElement= new BasicElement();
        byte[] bytes = "01A        ".getBytes(StandardCharsets.UTF_8);

        basicElement.setElementValue(bytes);
//        basicElement.setElementLength(new byte[]{0x00});

        Element element = isoProtoElement127Ex.createNewISOElement(basicElement);

        BasicElement basicElement1 = isoProtoElement127Ex.createBasicElement(element);
        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }

    @Test
    void createDE003 () throws Exception {
        SubElement subElement01 = isoProtoElement127Ex.getIsoProtoSubElementMap()
                .getSubElement("DE127S001").createNewISOSubElement("01");
        SubElement subElement02 = isoProtoElement127Ex.getIsoProtoSubElementMap()
                .getSubElement("DE127S002").createNewISOSubElement("B        ");

        List<SubElement> subElementList = new ArrayList<>();
        subElementList.add(subElement01);
        subElementList.add(subElement02);
        SubElementMap subElementMap = isoProtoElement127Ex.getIsoProtoSubElementMap().createNewSubElementMap(subElementList);
        Element element = isoProtoElement127Ex.createNewISOElement(subElementMap);

        BasicElement basicElement= new BasicElement();
        byte[] bytes = "01B        ".getBytes(StandardCharsets.UTF_8);


        basicElement.setElementValue(bytes);

        BasicElement basicElement1 = isoProtoElement127Ex.createBasicElement(element);

        assertAll("DE003 Tests",
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())
        );
    }

}
