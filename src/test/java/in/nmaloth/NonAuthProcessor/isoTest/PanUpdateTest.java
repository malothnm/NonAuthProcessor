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
import com.nithin.iso8583.iso.elementdef.subelement.base.ISOProtoSubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElement;
import com.nithin.iso8583.iso.elementdef.subelement.base.SubElementMap;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.DataSetEncoding;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.ISOProtoSubElementDataSet;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.ISOProtoSubElementDataSetMap;
import com.nithin.iso8583.iso.elementdef.subelement.dataset.SubElementDataSet;
import com.nithin.iso8583.iso.elementdef.subelement.tlv.ISOProtoSubElementTLV;
import com.nithin.iso8583.iso.elementdef.subelement.tlv.ISOProtoSubElementTLVMap;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PanUpdateTest {

    private ISOProtoElement isoProtoElement;

    public PanUpdateTest(){
        LoadingConfig loadingConfig = new LoadingConfig();
        isoProtoElement = loadingConfig.createProtoElement(127,"/isomessage/DE127/pan-maintenance");

    }

    @Test
    void getId() {
        assertEquals("DE127", isoProtoElement.getId());
    }

    @Test
    void getElementType() {
        assertEquals(ElementType.VARIABLE, isoProtoElement.getElementType());
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
        assertEquals(1,isoProtoElement.getLengthOfLength());
    }

    @Test
    void getMaxLength() {
        assertEquals(256,isoProtoElement.getMaxLength());
    }

    @Test
    void isUsagePresent() {
        assertTrue(isoProtoElement.isUsagePresent());
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
    void getDatasetMap(){
        ISOProtoSubElementDataSetMap isoProtoSubElementDataSetMap =
                (ISOProtoSubElementDataSetMap)isoProtoElement.getIsoProtoSubElementMap();


        assertAll("DE55 Data set Testing",
                ()->assertEquals(DataSetEncoding.HEX,isoProtoSubElementDataSetMap.getDataSetEncoding()),
                ()->assertEquals(1,isoProtoSubElementDataSetMap.getDataSetIDLength()),
                ()->assertEquals(2,isoProtoSubElementDataSetMap.getLengthOfLength()),
                ()->assertEquals(SubElementType.DATA_SET,isoProtoSubElementDataSetMap.getSubElementType()),
                ()->assertEquals("DE127",isoProtoSubElementDataSetMap.getParentId()),
                ()->assertEquals(1,isoProtoSubElementDataSetMap.getAllSubElements().size())
        );

    }

    @Test
    void getDatasetID41(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        assertAll("DataSet ID 41",
                ()->assertEquals(ElementType.VARIABLE,isoProtoSubElement.getElementType()),
                ()->assertEquals(EncodingType.SUB_ELEMENT,isoProtoSubElement.getIsoSourceType()),
                ()->assertEquals(ISOElementType.SUB_ELEMENT,isoProtoSubElement.getIsoTargetType()),
                ()->assertEquals(2,isoProtoSubElement.getLengthOfLength()),
                ()->assertEquals(256,isoProtoSubElement.getMaxLength()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElement.getPaddingType()),
                ()->assertEquals(SubElementType.DATA_SET,isoProtoSubElement.getSubElementType())

        );
    }


    @Test
    void getTLVTag41_01(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("01");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.HEX,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(19,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.VARIABLE,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }

    @Test
    void getTLVTag41_02(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("02");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.HEX,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(2,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }

    @Test
    void getTLVTag41_04(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("04");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(1,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }

    @Test
    void getTLVTag41_05(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("04");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(1,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }

    @Test
    void getTLVTag41_06(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("06");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(5,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }
    @Test
    void getTLVTag41_07(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("07");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(1,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }
    @Test
    void getTLVTag56_08(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("08");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.INT,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.BCD,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(1,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }

    @Test
    void getTLVTag41_09(){

        ISOProtoSubElementMap isoProtoSubElementDataSetMap =
                isoProtoElement.getIsoProtoSubElementMap();

        ISOProtoSubElementDataSet isoProtoSubElement = (ISOProtoSubElementDataSet)isoProtoSubElementDataSetMap
                .getSubElement("41");

        ISOProtoSubElementTLVMap isoProtoSubElementTLVMap = isoProtoSubElement.getIsoProtoSubElementTLVMap();
        ISOProtoSubElementTLV isoProtoSubElementTLV = (ISOProtoSubElementTLV) isoProtoSubElementTLVMap.getSubElement("09");

        assertAll("SubElement Tests",
                ()->assertEquals("DE127",isoProtoSubElementTLV.getParentId()),
                ()->assertEquals(SubElementType.TLV,isoProtoSubElementTLV.getSubElementType()),
                ()->assertEquals(ISOElementType.STRING,isoProtoSubElementTLV.getIsoTargetType()),
                ()->assertEquals(EncodingType.ASCII,isoProtoSubElementTLV.getIsoSourceType()),
                ()->assertEquals(1,isoProtoSubElementTLV.getMaxLength()),
                ()->assertFalse(isoProtoSubElementTLV.isToBeEncrypted()),
                ()->assertEquals(PaddingType.NONE,isoProtoSubElementTLV.getPaddingType()),
                ()->assertEquals(ElementType.FIXED,isoProtoSubElementTLV.getElementType()),
                ()->assertEquals(0,isoProtoSubElementTLV.getLengthOfLength())

        );

    }


    @Test
    void  subElementLengthValue() throws Exception {

        BasicElement basicElement= new BasicElement();

        //ds56 length --> 56


        //length --> 6
        byte[] tag41_01 = new byte[]{0x01};
        byte[] tag41_01Length = Utils.integerToByte(8,1);
        byte[] tag41_01Value = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,0x12,0x34,0x56};

        //length --> 7
        byte[] tag41_02 = new byte[]{0x02};
        byte[] tag41_02Length = Utils.integerToByte(2,1);
        byte[] tag41_02Value = new byte[]{0x22,0x11};

        String tag41_04_String = "A";
        byte[] tag41_04 = new byte[]{0x04};
        byte[] tag41_04Length =new byte[]{0x01};
        byte[] tag41_04Value = tag41_04_String.getBytes(StandardCharsets.UTF_8);

        byte[] tag41_05 = new byte[]{0x05};
        byte[] tag41_05Length =new byte[]{0x01};
        byte[] tag41_05Value = "V".getBytes(StandardCharsets.UTF_8);

        //length --> 4
        String conversionCode = "ABCDE";
        byte[] tag41_06 = new byte[]{0x06};
        byte[] tag41_06Length =Utils.integerToByte(conversionCode.length(),1);
        byte[] tag41_06Value = conversionCode.getBytes();


        //length --> 11
        String tag41_07_StringValue = "N";
        byte[] tag41_07 = new byte[]{0x07};
        byte[] tag41_07Length = Utils.integerToByte(tag41_07_StringValue.length(),1);
        byte[] tag41_07Value = tag41_07_StringValue.getBytes();


        //length --> 3
        byte[] tag41_08 = new byte[]{0x08};
        byte[] tag41_08Length =new byte[]{0x01};
        byte[] tag41_08Value = new byte[]{0x01};

        //length --> 5
        String tag41_09_StringValue = "Y";
        byte[] tag41_09 = new byte[]{0x09};
        byte[] tag41_09Length = Utils.integerToByte(tag41_09_StringValue.length(),1);
        byte[] tag41_09Value = tag41_09_StringValue.getBytes();






        byte[] bytes_41_value = Bytes.concat(
                tag41_01,tag41_01Length,tag41_01Value,
                tag41_02,tag41_02Length,tag41_02Value,
                tag41_04,tag41_04Length,tag41_04Value,
                tag41_05,tag41_05Length,tag41_05Value,
                tag41_06,tag41_06Length,tag41_06Value,
                tag41_07,tag41_07Length,tag41_07Value,
                tag41_08,tag41_08Length,tag41_08Value,
                tag41_09,tag41_09Length,tag41_09Value);

        byte[]  dataSetID41 = new byte[]{0x41};
        byte[] dataSetLength41 = Utils.integerToByte(bytes_41_value.length,2);


        byte[] bytes = Bytes.concat(dataSetID41,dataSetLength41,bytes_41_value);

        basicElement.setElementValue(bytes);
        basicElement.setElementLength(Utils.integerToByte(bytes_41_value.length + 3,1));

        Element element = isoProtoElement.createNewISOElement(basicElement);



        Map<String, SubElement> subElementMap = ((SubElementLengthValue)element.getElementLengthValue()).getValue()
                .getSubElementMap();


        SubElementDataSet subElementDS41 = ((SubElementDataSet) subElementMap.get("41"));




        SubElement subElement01 = subElementDS41.getSubElementMap().getSubElement("01");
        SubElement subElement02 = subElementDS41.getSubElementMap().getSubElement("02");
        SubElement subElement04 = subElementDS41.getSubElementMap().getSubElement("04");
        SubElement subElement05 = subElementDS41.getSubElementMap().getSubElement("05");
        SubElement subElement06 = subElementDS41.getSubElementMap().getSubElement("06");
        SubElement subElement07 = subElementDS41.getSubElementMap().getSubElement("07");
        SubElement subElement08 = subElementDS41.getSubElementMap().getSubElement("08");
        SubElement subElement09 = subElementDS41.getSubElementMap().getSubElement("09");



        assertAll("bit Map 56 test",
                ()-> assertEquals(8,subElementDS41.getSubElementMap().getAllSubElements().size()),
                ()->assertEquals("1234567890123456",((StringElementLengthValue)subElement01.getElementLengthValue()).getValue()),
                ()->assertEquals(8,subElement01.getElementLengthValue().getLength()),
                ()->assertEquals("2211",((StringElementLengthValue)subElement02.getElementLengthValue()).getValue()),
                ()->assertEquals(2,subElement02.getElementLengthValue().getLength()),
                ()->assertEquals("A",((StringElementLengthValue)subElement04.getElementLengthValue()).getValue()),
                ()->assertEquals(1,subElement04.getElementLengthValue().getLength()),
                ()->assertEquals("V",((StringElementLengthValue)subElement05.getElementLengthValue()).getValue()),
                ()->assertEquals(1,subElement05.getElementLengthValue().getLength()),
                ()->assertEquals("ABCDE",((StringElementLengthValue)subElement06.getElementLengthValue()).getValue()),
                ()->assertEquals(5,subElement06.getElementLengthValue().getLength()),
                ()->assertEquals("N",((StringElementLengthValue)subElement07.getElementLengthValue()).getValue()),
                ()->assertEquals(1,subElement07.getElementLengthValue().getLength()),
                ()->assertEquals(1,((IntElementLengthValue)subElement08.getElementLengthValue()).getValue()),
                ()->assertEquals(1,subElement08.getElementLengthValue().getLength()),
                ()->assertEquals("Y",((StringElementLengthValue)subElement09.getElementLengthValue()).getValue()),
                ()->assertEquals(1,subElement09.getElementLengthValue().getLength())


        );
    }




    @Test
    void  convertToBytes() throws Exception {

        BasicElement basicElement= new BasicElement();

        byte[] tag41_01 = new byte[]{0x01};
        byte[] tag41_01Length = Utils.integerToByte(8,1);
        byte[] tag41_01Value = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,0x12,0x34,0x56};

        //length --> 7
        byte[] tag41_02 = new byte[]{0x02};
        byte[] tag41_02Length = Utils.integerToByte(2,1);
        byte[] tag41_02Value = new byte[]{0x22,0x11};

        String tag41_04_String = "A";
        byte[] tag41_04 = new byte[]{0x04};
        byte[] tag41_04Length =new byte[]{0x01};
        byte[] tag41_04Value = tag41_04_String.getBytes(StandardCharsets.UTF_8);

        byte[] tag41_05 = new byte[]{0x05};
        byte[] tag41_05Length =new byte[]{0x01};
        byte[] tag41_05Value = "V".getBytes(StandardCharsets.UTF_8);

        //length --> 4
        String conversionCode = "ABCDE";
        byte[] tag41_06 = new byte[]{0x06};
        byte[] tag41_06Length =Utils.integerToByte(conversionCode.length(),1);
        byte[] tag41_06Value = conversionCode.getBytes();


        //length --> 11
        String tag41_07_StringValue = "N";
        byte[] tag41_07 = new byte[]{0x07};
        byte[] tag41_07Length = Utils.integerToByte(tag41_07_StringValue.length(),1);
        byte[] tag41_07Value = tag41_07_StringValue.getBytes();


        //length --> 3
        byte[] tag41_08 = new byte[]{0x08};
        byte[] tag41_08Length =new byte[]{0x01};
        byte[] tag41_08Value = new byte[]{0x01};

        //length --> 5
        String tag41_09_StringValue = "Y";
        byte[] tag41_09 = new byte[]{0x09};
        byte[] tag41_09Length = Utils.integerToByte(tag41_09_StringValue.length(),1);
        byte[] tag41_09Value = tag41_09_StringValue.getBytes();






        byte[] bytes_41_value = Bytes.concat(
                tag41_01,tag41_01Length,tag41_01Value,
                tag41_02,tag41_02Length,tag41_02Value,
                tag41_04,tag41_04Length,tag41_04Value,
                tag41_05,tag41_05Length,tag41_05Value,
                tag41_06,tag41_06Length,tag41_06Value,
                tag41_07,tag41_07Length,tag41_07Value,
                tag41_08,tag41_08Length,tag41_08Value,
                tag41_09,tag41_09Length,tag41_09Value);

        byte[]  dataSetID41 = new byte[]{0x41};
        byte[] dataSetLength41 = Utils.integerToByte(bytes_41_value.length,2);


        byte[] bytes = Bytes.concat(dataSetID41,dataSetLength41,bytes_41_value);

        basicElement.setElementValue(bytes);
        basicElement.setElementLength(Utils.integerToByte(bytes.length ,1));

        Element element = isoProtoElement.createNewISOElement(basicElement);

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);

        String convertedString = Utils.hexToString(basicElement1.getElementValue());

        System.out.println(convertedString);
        System.out.println(Utils.hexToString(basicElement.getElementValue()));

        assertAll("DE41 tagSet",
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength()),
                ()->assertArrayEquals(basicElement.getElementValue(),basicElement1.getElementValue())

        );

    }

    @Test
    void  testDE127() throws Exception {

        BasicElement basicElement= new BasicElement();

        ISOProtoSubElementDataSet isoProtoSubElementDataSet41 = (ISOProtoSubElementDataSet)isoProtoElement.getIsoProtoSubElementMap()
                .getSubElement("41");


//        ds01 length --> 34

        byte[]  dataSetID01 = new byte[]{0x01};
        byte[] dataSetLength01= new byte[]{0x00,0x1F};

        SubElement subElementDS4101 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("01")
                .createNewISOSubElement("1234567890123456")
                ;

        SubElement subElementDS4102 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("02")
                .createNewISOSubElement("2211")
                ;

        SubElement subElementDS4104 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("04")
                .createNewISOSubElement("A")
                ;

        SubElement subElementDS4105 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("05")
                .createNewISOSubElement("V")
                ;

        SubElement subElementDS4106 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("06")
                .createNewISOSubElement("ABCDE")
                ;

        SubElement subElementDS4107 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("07")
                .createNewISOSubElement("N")
                ;

        SubElement subElementDS4108 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("08")
                .createNewISOSubElement(2)
                ;

        SubElement subElementDS4109 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().getSubElement("09")
                .createNewISOSubElement("Y")
                ;

        List<SubElement> subElementListDS41 = new ArrayList<>();
        subElementListDS41.add(subElementDS4101);
        subElementListDS41.add(subElementDS4102);
        subElementListDS41.add(subElementDS4104);
        subElementListDS41.add(subElementDS4105);
        subElementListDS41.add(subElementDS4106);
        subElementListDS41.add(subElementDS4107);
        subElementListDS41.add(subElementDS4108);
        subElementListDS41.add(subElementDS4109);

        SubElementMap subElementMapDS41 = isoProtoSubElementDataSet41.getIsoProtoSubElementTLVMap().createNewSubElementMap(subElementListDS41);
        SubElement subElementDS41 = isoProtoSubElementDataSet41.createNewISOSubElement(subElementMapDS41);


        List<SubElement> subElementList = new ArrayList<>();
        subElementList.add(subElementDS41);
        SubElementMap subElementMap = isoProtoElement.getIsoProtoSubElementMap().createNewSubElementMap(subElementList);
        Element element = isoProtoElement.createNewISOElement(subElementMap);

        byte[] tag41_01 = new byte[]{0x01};
        byte[] tag41_01Length = Utils.integerToByte(8,1);
        byte[] tag41_01Value = new byte[]{0x12,0x34,0x56,0x78,(byte) 0x90,0x12,0x34,0x56};

        //length --> 7
        byte[] tag41_02 = new byte[]{0x02};
        byte[] tag41_02Length = Utils.integerToByte(2,1);
        byte[] tag41_02Value = new byte[]{0x22,0x11};

        String tag41_04_String = "A";
        byte[] tag41_04 = new byte[]{0x04};
        byte[] tag41_04Length =new byte[]{0x01};
        byte[] tag41_04Value = tag41_04_String.getBytes(StandardCharsets.UTF_8);

        byte[] tag41_05 = new byte[]{0x05};
        byte[] tag41_05Length =new byte[]{0x01};
        byte[] tag41_05Value = "V".getBytes(StandardCharsets.UTF_8);

        //length --> 4
        String conversionCode = "ABCDE";
        byte[] tag41_06 = new byte[]{0x06};
        byte[] tag41_06Length =Utils.integerToByte(conversionCode.length(),1);
        byte[] tag41_06Value = conversionCode.getBytes();


        //length --> 11
        String tag41_07_StringValue = "N";
        byte[] tag41_07 = new byte[]{0x07};
        byte[] tag41_07Length = Utils.integerToByte(tag41_07_StringValue.length(),1);
        byte[] tag41_07Value = tag41_07_StringValue.getBytes();


        //length --> 3
        byte[] tag41_08 = new byte[]{0x08};
        byte[] tag41_08Length =new byte[]{0x01};
        byte[] tag41_08Value = new byte[]{0x01};

        //length --> 5
        String tag41_09_StringValue = "Y";
        byte[] tag41_09 = new byte[]{0x09};
        byte[] tag41_09Length = Utils.integerToByte(tag41_09_StringValue.length(),1);
        byte[] tag41_09Value = tag41_09_StringValue.getBytes();






        byte[] bytes_41_value = Bytes.concat(
                tag41_01,tag41_01Length,tag41_01Value,
                tag41_02,tag41_02Length,tag41_02Value,
                tag41_04,tag41_04Length,tag41_04Value,
                tag41_05,tag41_05Length,tag41_05Value,
                tag41_06,tag41_06Length,tag41_06Value,
                tag41_07,tag41_07Length,tag41_07Value,
                tag41_08,tag41_08Length,tag41_08Value,
                tag41_09,tag41_09Length,tag41_09Value);

        byte[]  dataSetID41 = new byte[]{0x41};
        byte[] dataSetLength41 = Utils.integerToByte(bytes_41_value.length,2);


        byte[] bytes = Bytes.concat(dataSetID41,dataSetLength41,bytes_41_value);



        basicElement.setElementValue(bytes);
        basicElement.setElementLength(Utils.integerToByte(bytes.length,1));

        BasicElement basicElement1 = isoProtoElement.createBasicElement(element);

        String convertedString = Utils.hexToString(basicElement1.getElementValue());

        System.out.println(convertedString);
        System.out.println(Utils.hexToString(basicElement.getElementValue()));

        assertAll("DE56 tagSet",
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength()),
                ()->assertArrayEquals(basicElement.getElementLength(),basicElement1.getElementLength())

        );

    }

}
