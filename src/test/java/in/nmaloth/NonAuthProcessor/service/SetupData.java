package in.nmaloth.NonAuthProcessor.service;

import com.google.common.primitives.Bytes;
import com.nithin.iso8583.iso.elementdef.common.constants.PaddingType;
import com.nithin.iso8583.iso.elementdef.util.Utils;
import in.nmaloth.payments.constants.*;
import in.nmaloth.payments.constants.network.NetworkAdviceInit;
import in.nmaloth.payments.constants.network.NetworkKeyExchange;
import in.nmaloth.payments.constants.network.NetworkMessageType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class SetupData {

    byte[] authMessage;
    byte[] length = new byte[]{0x0B};
//    byte[] headerBytes = Bytes.concat(length, "HeaderTest".getBytes());
    byte[] mtiBytes = new byte[]{0x01, 0x00};
    byte[] de002 = getDE002();
    byte[] de004 = getDE004();
    byte[] de006 = getDE006();
    byte[] de007 = getDE007();
    byte[] de010 = getDE010();
    byte[] de011 = getDE011();
    byte[] de012 = getDE012();
    byte[] de013 = getDE013();
    byte[] de014 = getDE014();
    byte[] de018 = getDE018();
    byte[] de019 = getDE019();
    byte[] de020 = getDE020();
    byte[] de023 = getDE023();
    byte[] de026 = getDE026();
    byte[] de028 = getDE028();
    byte[] de032 = getDE032();
    byte[] de033 = getDE033();
    byte[] de035 = getDE035();
    byte[] de037 = getDE037();
    byte[] de038 = getDE038();
    byte[] de039 = getDE039();
    byte[] de041 = getDE041();
    byte[] de042 = getDE042();
    byte[] de043 = getDE043();
    byte[] de044 = getDE044();
    byte[] de045 = getDE045();
    byte[] de048 = getDE048();
    //    byte[] de049 = getDE049();
//    byte[] de051 = getDE051();
    byte[] de052 = getDE052();
    byte[] de053 = getDE053();
    byte[] de054 = getDE054();
    byte[] de055 = getDE055();
    byte[] de056 = getDE056();
    byte[] de059 = getDE059();
    byte[] de060 = getDE060();
    byte[] de061 = getDE061();
    byte[] de062 = getDE062();
    byte[] de063 = getDE063();
    byte[] de068 = getDE068();
    byte[] de070 = getDE070();
    byte[] de073 = getDE073();
    byte[] de090 = getDE090();
    byte[] de091 = getDE091();
    byte[] de092 = getDE092();
    byte[] de095 = getDE095();
    byte[] de100 = getDE100();
    byte[] de101 = getDE101();
    byte[] de102 = getDE102();
    byte[] de103 = getDE103();
    byte[] de115 = getDE115();
    byte[] de117 = getDE117();
    byte[] de118 = getDE118();
    byte[] de120 = getDE120();
    byte[] de121 = getDE121();
    byte[] de125 = getDE125();
    byte[] de126 = getDE126();

    private byte[] headerBytes(){

        return new byte[]{
                0x16,0x01,0x0C,0x01,0x12,0x01,0x23,
                0x34,0x45,0x67,0x78,0x23,0x12,0x23,0x01,0x02,0x03,0x0A,0x04,0x05,0x06,0x0B
        };
    }

    private byte[] headerBytesReject(){

        return new byte[]{
                0x1A,0x01,0x0C,0x01,0x12,0x01,0x23,0x34,0x45,0x67,
                0x78,0x23,0x12,0x23,0x01,0x02,0x03,0x0A,0x04,0x05,0x06,0x0B,0x1A,0x1B,0x1C,0x1D,
                0x16,0x01,0x0C,0x01,0x12,0x01,0x23,
                0x34,0x45,0x67,0x78,0x23,0x12,0x23,0x01,0x02,0x03,0x0A,0x04,0x05,0x06,0x0B
        };
    }


    public byte[] createNetworkMessages(NetworkMessageType networkMessages,
                                        NetworkAdviceInit networkAdviceInit, NetworkKeyExchange networkKeyExchange) {
        mtiBytes = new byte[]{0x08, 0x00};
        BitSet bitSet = new BitSet(128);
        bitSet.set(0);
        bitSet.set(6);
        bitSet.set(10);
        bitSet.set(36);
        bitSet.set(69);

        byte[] de70 = new byte[]{0x00, 0x00};

        switch (networkMessages) {
            case ECHO: {
                de70 = new byte[]{0x03, 0x01};
                break;
            }
            case GROUP_SIGN_ON: {
                de70 = new byte[]{0x00, 0x71};
                break;
            }
            case GROUP_SIGN_OFF: {
                de70 = new byte[]{0x00, 0x72};
                break;
            }
        }
        switch (networkAdviceInit) {
            case START_ADVICE: {
                de70 = new byte[]{0x00, 0x78};
                break;
            }
            case STOP_ADVICE: {
                de70 = new byte[]{0x00, 0x79};
            }
        }


        byte[] bytesBitSet = Utils.createByteArrayFromBitSet(bitSet, 16);

        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);


        return Bytes.concat(authMessage, getDE007(), getDE011(), getDE037(), de70);
    }

    public byte[] createNetworkMessagesResponse(NetworkMessageType networkMessages,
                                        NetworkAdviceInit networkAdviceInit,
                                                NetworkKeyExchange networkKeyExchange,String responseCode) {
        mtiBytes = new byte[]{0x08, 0x10};
        BitSet bitSet = new BitSet(128);
        bitSet.set(0);
        bitSet.set(6);
        bitSet.set(10);
        bitSet.set(36);
        bitSet.set(38);
        bitSet.set(69);

        byte[] de70 = new byte[]{0x00, 0x00};

        byte[] de39 = responseCode.getBytes(StandardCharsets.UTF_8);

        switch (networkMessages) {
            case ECHO: {
                de70 = new byte[]{0x03, 0x01};
                break;
            }
            case GROUP_SIGN_ON: {
                de70 = new byte[]{0x00, 0x71};
                break;
            }
            case GROUP_SIGN_OFF: {
                de70 = new byte[]{0x00, 0x72};
                break;
            }
        }
        switch (networkAdviceInit) {
            case START_ADVICE: {
                de70 = new byte[]{0x00, 0x78};
                break;
            }
            case STOP_ADVICE: {
                de70 = new byte[]{0x00, 0x79};
            }
        }


        byte[] bytesBitSet = Utils.createByteArrayFromBitSet(bitSet, 16);

        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);


        return Bytes.concat(authMessage, getDE007(), getDE011(), getDE037(), de39,de70);
    }

    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode,
                                    String verification,
                                    String txnCurrencyCode,
                                    String billingCurrencyCode, int mti) {



        mtiBytes = Utils.integerToHex(mti,2);

        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);

        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType, recurringTrans, pinEntryMode, verification, txnCurrencyCode, billingCurrencyCode);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }


    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode,
                                    String verification,
                                    String txnCurrencyCode,
                                    String billingCurrencyCode, int testNumber, int mti) {


        mtiBytes = Utils.integerToHex(mti,2);
        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);

        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType, recurringTrans, pinEntryMode, verification, txnCurrencyCode, billingCurrencyCode, testNumber);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }

    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode,
                                    String verification,
                                    String txnCurrencyCode,
                                    String billingCurrencyCode, int testNumber, int mti,String instrument) {


        mtiBytes = Utils.integerToHex(mti,2);
        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);

        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType, recurringTrans, pinEntryMode, verification, txnCurrencyCode, billingCurrencyCode, testNumber, instrument);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }


    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode,
                                    String verification,
                                    String txnCurrencyCode,
                                    String billingCurrencyCode, String fromAccount, int mti) {



        mtiBytes = Utils.integerToHex(mti,2);

        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);

        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType, recurringTrans, pinEntryMode, verification, txnCurrencyCode, billingCurrencyCode, fromAccount);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }


    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode, String verification,
                                    TerminalType terminalType,
                                    AuthorizationType authorizationType,
                                    String txnCurrencyCode, String billingCurrencyCode,
                                    int mti
    ) {


        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);

        mtiBytes = Utils.integerToHex(mti,2);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);


        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType,
                    recurringTrans, pinEntryMode, verification, terminalType, authorizationType, txnCurrencyCode, billingCurrencyCode);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }


    public byte[] createAuthMessage(Integer[] integers, TransactionType transactionType,
                                    TerminalPinCapability terminalPinCapability,
                                    EntryMode entryMode,
                                    AVSType avsType,
                                    RecurringTrans recurringTrans,
                                    PinEntryMode pinEntryMode, String verification,
                                    TerminalType terminalType,
                                    AuthorizationType authorizationType,
                                    InstallmentType installmentType,
                                    String txnCurrencyCode,
                                    String billingCurrencyCode,
                                    int mti
    ) {


        List<Integer> integerList = Arrays.asList(integers);
        Collections.sort(integerList);
        byte[] bytesBitSet = getBitMapBytes(integerList);

        mtiBytes = Utils.integerToHex(mti,2);


        byte[] authMessage = Bytes.concat(headerBytes(), mtiBytes, bytesBitSet);


        for (Integer integer : integerList) {
            byte[] deData = getData(integer, transactionType, terminalPinCapability, entryMode, avsType,
                    recurringTrans, pinEntryMode, verification, terminalType, authorizationType, installmentType, txnCurrencyCode, billingCurrencyCode);
            authMessage = Bytes.concat(authMessage, deData);
        }

        return authMessage;
    }


    public byte[] getDE055() {


        byte[] tag71 = new byte[]{0x71};
        byte[] tag71Value = new byte[]{0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09};
        byte[] tag71Length = Utils.integerToByte(tag71Value.length, 1);

        byte[] tag72 = new byte[]{0x72};
        byte[] tag72Value = new byte[]{0x07, 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34};
        byte[] tag72Length = Utils.integerToByte(tag72Value.length, 1);

        byte[] tag82 = new byte[]{(byte) 0x82};
        byte[] tag82Value = new byte[]{(byte) 0xA0, 0x30};
        byte[] tag82Length = Utils.integerToByte(tag82Value.length, 1);


        //length --> 9
        byte[] tag9F02 = new byte[]{(byte) 0x9F, 0x02};
        byte[] tag9F02Value = new byte[]{0x00, 0x00, 0x00, 0x00, 0x10, 0x00};
        byte[] tag9F02Length = Utils.integerToByte(tag9F02Value.length, 1);

        //length ---> 9
        byte[] tag9F03 = new byte[]{(byte) 0x9F, 0x03};
        byte[] tag9F03Value = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x10};
        byte[] tag9F03Length = Utils.integerToByte(tag9F03Value.length, 1);
        ;


        //length ----> 5
        byte[] tag9F36 = new byte[]{(byte) 0x9F, 0x36};
        byte[] tag9F36Length = new byte[]{0x02};
        byte[] tag9F36Value = new byte[]{0x01, 0x01};

        //length ----> 7
        byte[] tag95 = new byte[]{(byte) 0x95};
        byte[] tag95Length = new byte[]{0x05};
        byte[] tag95Value = new byte[]{0x01, 0x01, 0x01, 0x01, 0x01};

        //length ---> 4
        byte[] tag9A = new byte[]{(byte) 0x9A};
        byte[] tag9ALength = new byte[]{0x02};
        byte[] tag9AValue = new byte[]{0x05, 0x03};

        //length ---> 10
        byte[] tag91 = new byte[]{(byte) 0x91};
        byte[] tag91Length = new byte[]{0x08};
        byte[] tag91Value = new byte[]{0x07, 0x01, 0x0A, 0x00, 0x00, (byte) 0xBC, 0x00, 0x00};

        //length ---> 10
        byte[] tag84 = new byte[]{(byte) 0x84};
        byte[] tag84Length = new byte[]{0x08};
        byte[] tag84Value = "TestFile".getBytes();

        byte[] tag9C = new byte[]{(byte) 0x9C};
        byte[] tag9CValue = new byte[]{0x00, 0x05};
        byte[] tag9CLength = Utils.integerToByte(tag9CValue.length, 1);

        byte[] tagC0 = new byte[]{(byte) 0xC0};
        byte[] tagC0Value = new byte[]{(byte) 0xFF, 0X12, 0x56, 0x78, 0x33, 0x22, 0x11, 0x56};
        byte[] tagC0Length = Utils.integerToByte(tagC0Value.length, 1);

        byte[] tag5F2A = new byte[]{0x5F, 0x2A};
        byte[] tag5F2AValue = new byte[]{0x04, (byte) 0x84};
        byte[] tag05F2ALength = Utils.integerToByte(tag5F2AValue.length, 1);

        byte[] tag9F10 = new byte[]{(byte) 0x9F, 0x10};
        byte[] tag9F10Value = new byte[]{0x06, 0x01, 0x0A, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, 0x01};
        byte[] tag9F10Length = Utils.integerToByte(tag9F10Value.length, 1);

        byte[] tag9F1A = new byte[]{(byte) 0x9F, 0x1A};
        byte[] tag9F1AValue = new byte[]{0x08, 0x40};
        byte[] tag9F1ALength = Utils.integerToByte(tag9F1AValue.length, 1);


        byte[] tag9F26 = new byte[]{(byte) 0x9F, 0x26};
        byte[] tag9F26Value = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88};
        byte[] tag9F26Length = Utils.integerToByte(tag9F26Value.length, 1);

        byte[] tag9F33 = new byte[]{(byte) 0x9F, 0x33};
        byte[] tag9F33Value = new byte[]{(byte) 0xF1, 0x02, (byte) 0xF3};
        byte[] tag9F33Length = Utils.integerToByte(tag9F33Value.length, 1);

        byte[] tag9F37 = new byte[]{(byte) 0x9F, 0x37};
        byte[] tag9F37Value = new byte[]{0x01, 0x02, 0x03, 0x04};
        byte[] tag9F37Length = Utils.integerToByte(tag9F37Value.length, 1);

        byte[] tag9F5B = new byte[]{(byte) 0x9F, 0x5B};
        byte[] tag9F5BValue = new byte[]{0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        byte[] tag9F5BLength = Utils.integerToByte(tag9F5BValue.length, 1);

        byte[] tag9F6E = new byte[]{(byte) 0x9F, 0x6E};
        byte[] tag9F6EValue = new byte[]{0x0A, 0x0B, 0x0C, 0x0D};
        byte[] tag9F6ELength = Utils.integerToByte(tag9F6EValue.length, 1);

        byte[] tag9F7C = new byte[]{(byte) 0x9F, 0x7C};
        byte[] tag9F7CValue = new byte[]{0x05, 0x12, 0x34, 0x56, 0x78, (byte) 0x90};
        byte[] tag9F7CLength = Utils.integerToByte(tag9F7CValue.length, 1);


        byte[] bytestags = Bytes.concat(
                tag71, tag71Length, tag71Value,
                tag72, tag72Length, tag72Value,
                tag82, tag82Length, tag82Value,
                tag84, tag84Length, tag84Value,
                tag91, tag91Length, tag91Value,
                tag95, tag95Length, tag95Value,
                tag9A, tag9ALength, tag9AValue,
                tag9C, tag9CLength, tag9CValue,
                tagC0, tagC0Length, tagC0Value,
                tag5F2A, tag05F2ALength, tag5F2AValue,
                tag9F02, tag9F02Length, tag9F02Value,
                tag9F03, tag9F03Length, tag9F03Value,
                tag9F10, tag9F10Length, tag9F10Value,
                tag9F1A, tag9F1ALength, tag9F1AValue,
                tag9F26, tag9F26Length, tag9F26Value,
                tag9F33, tag9F33Length, tag9F33Value,
                tag9F36, tag9F36Length, tag9F36Value,
                tag9F37, tag9F37Length, tag9F37Value,
                tag9F5B, tag9F5BLength, tag9F5BValue,
                tag9F6E, tag9F6ELength, tag9F6EValue,
                tag9F7C, tag9F7CLength, tag9F7CValue
        );

        byte[] dataSetID = new byte[]{0x01};
        byte[] dataSetLength = Utils.integerToByte(bytestags.length, 2);


        byte[] bytes = Bytes.concat(
                dataSetID, dataSetLength, bytestags);

        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }


    public byte[] getDE063() {

        BitSet bitSet = new BitSet(3);
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(3);
        bitSet.set(18);

        byte[] bitSetBytes = Utils.createByteArrayFromBitSet(bitSet, 3);
        byte[] se01 = new byte[]{0x01, 0x23};
        byte[] se02 = new byte[]{0x10, 0x10};
        byte[] se03 = new byte[]{0x25, 0x01};
        byte[] se04 = new byte[]{(byte) 0x90, 0x47};
        byte[] se19 = "ABC".getBytes();

        byte[] bytes = Bytes.concat(bitSetBytes, se01, se02, se03, se04, se19);

        byte[] bytesLength = new byte[]{0x0E};
        return Bytes.concat(bytesLength, bytes);

    }

    public byte[] getDE063(TransactionType transactionType, AuthorizationType authorizationType) {

        BitSet bitSet = new BitSet(3);
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(3);
        bitSet.set(18);

        byte[] bitSetBytes = Utils.createByteArrayFromBitSet(bitSet, 3);
        byte[] se01 = new byte[]{0x01, 0x23};
        byte[] se02 = new byte[]{0x10, 0x10};

        byte[] se03;
        if (transactionType.equals(TransactionType.TOKEN_ACTIVATION_REQUEST)) {
            se03 = new byte[]{0x37, 0x00};
        } else {
            se03 = new byte[]{0x25, 0x01};
        }

        byte[] se04 = new byte[]{(byte) 0x90, 0x47};
        byte[] se19 = "ABC".getBytes();

        byte[] bytes = Bytes.concat(bitSetBytes, se01, se02, se03, se04, se19);

        byte[] bytesLength = new byte[]{0x0E};
        return Bytes.concat(bytesLength, bytes);

    }


    public byte[] getDE044() {

        byte[] bytes = new byte[15];
        bytes[0] = "C".getBytes()[0];
        bytes[1] = "A".getBytes()[0];
        bytes[2] = " ".getBytes()[0];
        bytes[3] = " ".getBytes()[0];
        bytes[4] = "0".getBytes()[0];
        bytes[8] = "3".getBytes()[0];
        bytes[10] = "2".getBytes()[0];
        bytes[14] = "1".getBytes()[0];

        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }


    public byte[] getDE018(TransactionType transactionType, TerminalType terminalType) {

        if (transactionType.equals(TransactionType.CASH)) {

            if (terminalType.equals(TerminalType.ATM)) {
                return new byte[]{0x60, 0x11};
            } else {
                return new byte[]{0x60, 0x10};
            }

        }

        return new byte[]{0x54, 0x23};
    }


    public byte[] getDE018() {

        return new byte[]{0x60, 0x11};
    }

    public byte[] getDE014() {
        return new byte[]{0x20, 0x03};
    }

    public byte[] getDE013() {
        return new byte[]{0x03, 0x11};
    }

    public byte[] getDE012() {
        return new byte[]{0x18, 0x59, 0x11};
    }

    public byte[] getDE011() {
        return new byte[]{0x03, 0x11, 0x18};
    }

    public byte[] getDE007() {
        return new byte[]{0x03, 0x11, 0x18, 0x59, 0x11};
    }

    public byte[] getDE006() {
        return new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0x20, 0x00};
    }

    public byte[] getDE004() {
        return new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0x10, 0x00};
    }

    public byte[] getDE003(TransactionType transactionType) {

        byte[] bytes = new byte[3];

        switch (transactionType) {
            case CASH: {

                bytes[0] = 0x01;
                break;
            }
            case ACCOUNT_FUND_TRANSACTION: {
                bytes[0] = 0x10;
                break;
            }
            case QUASI_CASH: {
                bytes[0] = 0x11;
                break;
            }
            case CREDIT_VOUCHER:
            case MERCHANDISE_RETURN: {
                bytes[0] = 0x20;
                break;
            }
            case OCT: {
                bytes[0] = 0x26;
                break;
            }
            case PREPAID_LOAD: {
                bytes[0] = 0x28;
                break;
            }
            case BALANCE_INQUIRIES: {
                bytes[0] = 0x30;
                break;
            }
            case ELIGIBILITY_ENQUIRY: {
                bytes[0] = 0x39;
                break;
            }
            case BILL_PAYMENT: {
                bytes[0] = 0x50;
                break;
            }
            case PAYMENT: {
                bytes[0] = 0x53;
                break;
            }
            case PIN_CHANGE: {
                bytes[0] = 0x70;
                break;
            }
            case PREPAID_ACTIVATION:
            case PIN_UNBLOCK: {
                bytes[0] = 0x72;
                break;
            }
            default: {
                bytes[0] = 0x00;
                break;
            }
        }

        bytes[1] = 0x30;
        bytes[2] = 0x10;
        return bytes;
    }

    public byte[] getDE003(TransactionType transactionType, String fromAccount) {

        byte[] bytes = new byte[3];

        switch (transactionType) {
            case CASH: {

                bytes[0] = 0x01;
                break;
            }
            case ACCOUNT_FUND_TRANSACTION: {
                bytes[0] = 0x10;
                break;
            }
            case QUASI_CASH: {
                bytes[0] = 0x11;
                break;
            }
            case CREDIT_VOUCHER:
            case MERCHANDISE_RETURN: {
                bytes[0] = 0x20;
                break;
            }
            case OCT: {
                bytes[0] = 0x26;
                break;
            }
            case PREPAID_LOAD: {
                bytes[0] = 0x28;
                break;
            }
            case BALANCE_INQUIRIES: {
                bytes[0] = 0x30;
                break;
            }
            case ELIGIBILITY_ENQUIRY: {
                bytes[0] = 0x39;
                break;
            }
            case BILL_PAYMENT: {
                bytes[0] = 0x50;
                break;
            }
            case PAYMENT: {
                bytes[0] = 0x53;
                break;
            }
            case PIN_CHANGE: {
                bytes[0] = 0x70;
                break;
            }
            case PREPAID_ACTIVATION:
            case PIN_UNBLOCK: {
                bytes[0] = 0x72;
                break;
            }
            default: {
                bytes[0] = 0x00;
                break;
            }
        }

        bytes[1] = Utils.stringToHex(fromAccount, 2, "0", PaddingType.RIGHT)[0];
        bytes[2] = Utils.stringToHex(fromAccount, 2, "0", PaddingType.RIGHT)[0];
        return bytes;
    }

    public byte[] getBitMapBytes(List<Integer> integers) {

        int lastIndex = integers.size() - 1;

        int bitSetLength = 8;

        if (integers.get(lastIndex) > 63) {
            bitSetLength = 16;
        }
        BitSet bitSet = new BitSet(bitSetLength);

        if (bitSetLength > 8) {
            bitSet.set(0);
        }

        integers.forEach(integer -> bitSet.set(integer - 1));

        return Utils.createByteArrayFromBitSet(bitSet, bitSetLength);
    }

    public byte[] getDE002() {
        byte[] length = new byte[]{0x0F};
        byte[] de002 = new byte[]{0x02, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56};
        return Bytes.concat(length, de002);
    }

    public byte[] getDE002(String instrument) {
        byte[] length =Utils.integerToByte(instrument.length(),1);
        byte[] de002 = Utils.stringToHex(instrument,instrument.length(),"0",PaddingType.LEFT);
        return Bytes.concat(length, de002);
    }

    public byte[] getDE010() {
        return new byte[]{0x03, 0x11, 0x18, 0x59};
    }

    public byte[] getDE039() {
        return "05".getBytes();
    }

    public byte[] getDE039(int testNumber) {

        if(testNumber == 1){
            return "05".getBytes(StandardCharsets.UTF_8);
        }
        return "00".getBytes();
    }


    public byte[] getDE038() {
        return "123456".getBytes();
    }

    public byte[] getDE037() {
        return "123456789012".getBytes();
    }

    public byte[] getDE035() {

        byte[] bytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56, (byte) 0xd1, 0x22, 0x51, 0x11, 0x00, 0x00, 0x00, 0x00, 0x12, 0x30, 0x00};
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE033() {
        return Bytes.concat(new byte[]{0x04},
                new byte[]{0x11, 0x23, 0x45, 0x68});
    }

    public byte[] getDE032() {
        return Bytes.concat(new byte[]{0x04},
                new byte[]{0x00, 0x23, 0x45, 0x67});
    }

    public byte[] getDE028() {
        return "C00000240".getBytes();
    }

    public byte[] getDE026() {
        return new byte[]{0x01};
    }

    public byte[] getDE025(String verification) {

        if (verification != null && verification.equalsIgnoreCase("VER")) {
            return new byte[]{0x51};
        }
        return new byte[]{0x01};
    }

    public byte[] getDE025(String verification, EntryMode entryMode) {

        if (verification != null && verification.equalsIgnoreCase("VER")) {
            return new byte[]{0x51};
        }

        if (entryMode.equals(EntryMode.MOTO)) {
            return new byte[]{0x08};
        }

        if (entryMode.equals(EntryMode.ECOMM)) {
            return new byte[]{0x59};
        }

        return new byte[]{0x01};
    }

    public byte[] getDE023() {
        byte[] bytes = new byte[2];
        bytes[0] = 0x00;
        bytes[1] = 0x09;
        return bytes;
    }

    public byte[] getDE022(EntryMode entryMode, TerminalPinCapability terminalPinCapability) {


        byte[] bytes = new byte[2];
        bytes[0] = getEntryMode(entryMode);
        bytes[1] = getPinEntryCapability(terminalPinCapability);
        return bytes;

    }

    private byte getPinEntryCapability(TerminalPinCapability terminalPinCapability) {
        switch (terminalPinCapability) {
            case ACCEPTS_EITHER:
            case ACCEPTS_OFFLINE_PIN: {
                return 0x08;
            }
            case CANNOT_ACCEPT_PIN: {
                return 0x02;
            }
            case ACCEPTS_ONLINE_PIN: {
                return 0x01;
            }
            default: {
                return 0x00;
            }
        }
    }

    private byte getEntryMode(EntryMode entryMode) {
        switch (entryMode) {
            case MOTO:
            case ECOMM:
            case CHEQUE:
            case MANUAL_KEY_ENTRY: {
                return 0x01;
            }
            case MAG_CVV_NOT_POSSIBLE: {
                return 0x02;
            }
            case OPTICAL_CODE: {
                return 0x03;
            }
            case ICC: {
                return 0x05;
            }
            case WALLET_CONTACT_LESS_ICC:
            case CONTACT_LESS_ICC: {
                return 0x07;
            }
            case COF_TOKEN:
            case COF: {
                return 0x10;
            }
            case WALLET_CONTACT_MST:
            case MAG: {
                return (byte) 0x90;
            }
            case WALLET_CONTACT_LESS_MAG_STRIPE:
            case CONTACT_LESS_MAG: {
                return (byte) 0x91;
            }
            case ICC_INVALID_CVV_I_CVV: {
                return (byte) 0x95;
            }
            default: {
                return 0x00;
            }
        }
    }

    public byte[] getDE020() {
        return new byte[]{0x04, (byte) 0x84};
    }

    public byte[] getDE019() {
        return new byte[]{0x04, (byte) 0x84};
    }

    public byte[] getDE041() {
        return "12345678".getBytes();
    }

    public byte[] getDE042() {
        return "123456789012345".getBytes();
    }

    public byte[] getDE043() {
        return "1234567890123456789012345123456789012312".getBytes();
    }

    public byte[] getDE045() {

        String track1 = "B4000340000000504^John/Doe                  ^22251110000123000";
        int length = track1.length();
        return Bytes.concat(Utils.integerToByte(length, 1), track1.getBytes());
    }

    public byte[] getDE048() {
        return new byte[]{0x01};
    }

    public byte[] getDE049(String txnCurrencyCode) {

        return Utils.stringToHex(txnCurrencyCode, 3, "0", PaddingType.LEFT);
    }

    public byte[] getDE051(String billingCurrencyCode) {
        return Utils.stringToHex(billingCurrencyCode, 3, "0", PaddingType.LEFT);
    }

    public byte[] getDE052() {
        return new byte[]{0x08, 0x40, (byte) 0xab, (byte) 0xcd, 0x12, 0x45, 0x78, (byte) 0x87};
    }

    public byte[] getDE053() {
        return new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0x99, (byte) 0x99, (byte) 0x99};
    }

    public byte[] getDE054() {

        String field1 = "1001840C000010000000";
        String field2 = "2001484C000030000000";
        String field3 = "3001124D000040000000";
        String field4 = "4002840D000090000000";
        String field5 = "4002363D000980000000";
        String field6 = "4002363D000970000000";

        byte[] bytes = Bytes.concat(field1.getBytes(), field2.getBytes(), field3.getBytes(), field4.getBytes(), field5.getBytes(), field6.getBytes());
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE056() {

        byte[] dataSetID01 = new byte[]{0x01};
        byte[] dataSetLength01 = new byte[]{0x00, 0x1F};

        //length --> 31
        String tag01StringValue = "12345678901234567890123456789";
        byte[] tag01 = new byte[]{0x01};
        byte[] tag01Length = new byte[]{0x1D};
        byte[] tag01Value = tag01StringValue.getBytes();

        //ds02 length -> 36
        byte[] dataSetID02 = new byte[]{0x02};
        byte[] dataSetLength02 = new byte[]{0x00, 0x21};

        //length --> 18
        String tag83StringValue = "1234567890123456";
        byte[] tag83 = new byte[]{(byte) 0x83};
        byte[] tag83Length = new byte[]{0x10};
        byte[] tag83Value = tag83StringValue.getBytes();


        //length ---> 15
        String tag86StringValue = "testemail.com";
        byte[] tag86 = new byte[]{(byte) 0x86};
        byte[] tag86Value = tag86StringValue.getBytes();
        byte[] tag86Length = Utils.integerToByte(tag86Value.length, 1);


        byte[] bytes = Bytes.concat(
                dataSetID01, dataSetLength01,
                tag01, tag01Length, tag01Value,
                dataSetID02, dataSetLength02,
                tag83, tag83Length, tag83Value,
                tag86, tag86Length, tag86Value
        );

        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE059() {

        byte[] bytes = "682019".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE060() {
        byte[] bytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12};
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE060(String verification, EntryMode entryMode) {

        byte[] bytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12};

        switch (entryMode) {
            case COF_TOKEN:
            case WALLET_CONTACT_LESS_ICC:
            case WALLET_CONTACT_LESS_MAG_STRIPE:
            case WALLET_CONTACT_MST: {
                bytes[3] = 0x41;
            }

        }


        if (verification == null) {
            return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
        }
        switch (verification) {
            case "MOTO": {

                bytes[4] = 0x01;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "RECUR": {
                bytes[4] = 0x02;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "INSTALL": {
                bytes[4] = 0x03;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "UNKNOWNMAILORDER": {
                bytes[4] = 0x04;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

            }
            case "SECUREECOMM": {
                bytes[4] = 0x05;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NOT3D": {
                bytes[4] = 0x06;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NOAUTHSECURITYTRAN": {
                bytes[4] = 0x07;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NONSECURE": {
                bytes[4] = 0x08;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            default: {

            }

        }
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }

    public byte[] getDE060(String verification, EntryMode entryMode, TerminalType terminalType) {

        byte[] bytes = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12};


        switch (terminalType) {
            case UNATTENDED_CARDHOLDER_ACTIVATED_NO_AUTH: {
                bytes[0] = 0x10;
                break;
            }
            case ATM: {
                bytes[0] = 0x20;
                break;
            }
            case UNATTENDED_CARDHOLDER_AUTHORIZED: {
                bytes[0] = 0x30;
                break;
            }
            case ELECTRONIC_CASH_REGISTER: {
                bytes[0] = 0x40;
                break;
            }
            case HOME_TERMINALS: {
                bytes[0] = 0x50;
                break;
            }
            case MOBILE_ACCEPTANCE_SOLUTION: {
                bytes[0] = (byte) 0x90;
                break;
            }
            case MOTO: {
                bytes[0] = 0x70;
                break;
            }

        }


        switch (entryMode) {
            case COF_TOKEN:
            case WALLET_CONTACT_LESS_ICC:
            case WALLET_CONTACT_LESS_MAG_STRIPE:
            case WALLET_CONTACT_MST: {
                bytes[3] = 0x41;
                break;
            }

        }


        if (verification == null) {
            return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
        }
        switch (verification) {
            case "MOTO": {

                bytes[4] = 0x01;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "RECUR": {
                bytes[4] = 0x02;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "INSTALL": {
                bytes[4] = 0x03;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "UNKNOWNMAILORDER": {
                bytes[4] = 0x04;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

            }
            case "SECUREECOMM": {
                bytes[4] = 0x05;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NOT3D": {
                bytes[4] = 0x06;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NOAUTHSECURITYTRAN": {
                bytes[4] = 0x07;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            case "NONSECURE": {
                bytes[4] = 0x08;
                return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
            }
            default: {

            }

        }
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }

    public byte[] getDE061() {

        byte[] bytes1 = new byte[]{0x00, 0x01, 0x12, 0x78, (byte) 0x90, 0x12};
        byte[] bytes2 = new byte[]{0x00, 0x01, 0x34, 0x78, (byte) 0x90, 0x12};
        byte[] bytes3 = new byte[]{0x00, 0x01, 0x56, 0x78, (byte) 0x90, 0x12};


        byte[] bytes = Bytes.concat(bytes1, bytes2, bytes3);
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE062() {

        BitSet bitSet = new BitSet(8);
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(3);
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(16);
        bitSet.set(19);
        bitSet.set(20);
        bitSet.set(21);
        bitSet.set(22);
        bitSet.set(23);
        bitSet.set(24);
        bitSet.set(25);

        byte[] de001 = "A".getBytes();
        byte[] de002 = new byte[]{0x02, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56};
        byte[] de003 = "ABCD".getBytes();
        byte[] de004 = "B".getBytes();
        byte[] de005 = new byte[]{0x10};
        byte[] de006 = "C".getBytes();
        byte[] de017 = "123456789012345".getBytes();
        byte[] de020 = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90};
        byte[] de21 = "DEFG".getBytes();
        byte[] de22 = "ABCDEF".getBytes();
        byte[] de23 = "P1".getBytes();
        byte[] de24 = "P12345".getBytes();
        byte[] de25 = "S".getBytes();
        byte[] de26 = "D".getBytes();

        byte[] bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de001, de002, de003, de004, de005, de006, de017, de020, de21, de22, de23, de24, de25, de26);
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE062(int testNumber) {

        BitSet bitSet = new BitSet(8);
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        if (testNumber != 1) {
            bitSet.set(3);
        }
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(16);
        if (testNumber != 1) {
            bitSet.set(19);
        }
        bitSet.set(20);
        bitSet.set(21);
        if (testNumber != 1) {
            bitSet.set(22);
        }
        if (testNumber != 1) {
            bitSet.set(23);
        }
        if (testNumber != 1) {
            bitSet.set(24);
        }
        bitSet.set(25);

        byte[] de001 = "A".getBytes();
        byte[] de002 = null;

        de002 = new byte[]{0x02, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56};

        byte[] de003 = "ABCD".getBytes();
        byte[] de004 = null;
        if (testNumber != 1) {
            de004 = "B".getBytes();
        }
        byte[] de005 = new byte[]{0x10};
        byte[] de006 = "C".getBytes();
        byte[] de017 = "123456789012345".getBytes();
        byte[] de020 = null;
        if (testNumber != 1) {
            de020 = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90};
        }
        byte[] de21 = "DEFG".getBytes();
        byte[] de22 = "ABCDEF".getBytes();
        byte[] de23 = null;
        if (testNumber != 1) {
            de23 = "P1".getBytes();
        }
        byte[] de24 = null;
        if (testNumber != 1) {
            de24 = "P12345".getBytes();
        }
        byte[] de25 = null;

        if (testNumber != 1) {
            de25 = "S".getBytes();
        }
        byte[] de26 = "D".getBytes();

        byte[] bytes;
        if (testNumber == 1) {
            bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de001, de002,de003, de005, de006, de017, de21, de22, de26);

        } else {
            bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de001, de002, de003, de004, de005, de006, de017, de020, de21, de22, de23, de24, de25, de26);
        }
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }


    public byte[] getDE062(AuthorizationType authorizationType) {

        BitSet bitSet = new BitSet(8);
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(3);
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(16);
        bitSet.set(19);
        bitSet.set(20);
        bitSet.set(21);
        bitSet.set(22);
        bitSet.set(23);
        bitSet.set(24);
        bitSet.set(25);

        byte[] de001;
        if (authorizationType.equals(AuthorizationType.INCREMENTAL_AUTH)) {
            de001 = "I".getBytes();

        } else {
            de001 = "A".getBytes();

        }
        byte[] de002 = new byte[]{0x02, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56};
        byte[] de003 = "ABCD".getBytes();
        byte[] de004 = "B".getBytes();
        byte[] de005 = new byte[]{0x10};
        byte[] de006 = "C".getBytes();
        byte[] de017 = "123456789012345".getBytes();
        byte[] de020 = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90};
        byte[] de21 = "DEFG".getBytes();
        byte[] de22 = "ABCDEF".getBytes();
        byte[] de23 = "P1".getBytes();
        byte[] de24 = "P12345".getBytes();
        byte[] de25 = "S".getBytes();
        byte[] de26 = "D".getBytes();

        byte[] bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de001, de002, de003, de004, de005, de006, de017, de020, de21, de22, de23, de24, de25, de26);
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE068() {
        return new byte[]{0x10, 0x20};
    }

    public byte[] getDE070() {
        return new byte[]{0x10, 0x20};

    }

    public byte[] getDE073() {
        return new byte[]{0x21, 0x12, 0x31};
    }

    public byte[] getDE090() {
        byte[] de001 = new byte[]{0x01, 0x00};
        byte[] de002 = new byte[]{0x12, 0x34, 0x56};
        byte[] de003 = new byte[]{(byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99};
        byte[] de004 = new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12};

        byte[] bytes = Bytes.concat(de001, de002, de003, de004);
        return bytes;
    }

    public byte[] getDE091() {
        return "2".getBytes();
    }

    public byte[] getDE092() {
        return "12".getBytes();
    }


    public byte[] getDE095() {
        byte[] de001 = "000000050000".getBytes();
        byte[] de002 = "000000004000".getBytes();
        ;
        byte[] de003 = "000004500".getBytes();
        byte[] de004 = "000002500".getBytes();

        byte[] bytes = Bytes.concat(de001, de002, de003, de004);
        return bytes;
    }


    public byte[] getDE100() {
        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE101() {

        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE102() {

        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE103() {

        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE115() {

        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);

    }

    public byte[] getDE117() {
        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE118() {
        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE121() {
        byte[] bytes = "test".getBytes();
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
    }

    public byte[] getDE120() {

        //ds56 length --> 56

        byte[] dataSetID56 = new byte[]{0x56};
        byte[] dataSetLength56 = new byte[]{0x00, 0x3E};

        //length --> 6
        String tag56_01_StringValue = "test";
        byte[] tag56_01 = new byte[]{0x01};
        byte[] tag56_01Length = Utils.integerToByte(tag56_01_StringValue.length(), 1);
        byte[] tag56_01Value = tag56_01_StringValue.getBytes();

        //length --> 7
        String tag56_02_StringValue = "test1";
        byte[] tag56_02 = new byte[]{0x02};
        byte[] tag56_02Length = Utils.integerToByte(tag56_02_StringValue.length(), 1);
        byte[] tag56_02Value = tag56_02_StringValue.getBytes();

        //length --> 3
        byte[] tag56_03 = new byte[]{0x03};
        byte[] tag56_03Length = new byte[]{0x01};
        byte[] tag56_03Value = new byte[]{0x09};

        //length --> 4
        byte[] tag56_04 = new byte[]{0x04};
        byte[] tag56_04Length = new byte[]{0x02};
        byte[] tag56_04Value = new byte[]{0x00, 0x07};

        //length --> 4
        String deviceCountry = "IN";
        byte[] tag56_05 = new byte[]{0x05};
        byte[] tag56_05Length = Utils.integerToByte(deviceCountry.length(), 1);
        byte[] tag56_05Value = deviceCountry.getBytes();


        //length --> 11
        String tag56_08_StringValue = "123456789";
        byte[] tag56_08 = new byte[]{0x08};
        byte[] tag56_08Length = Utils.integerToByte(tag56_08_StringValue.length(), 1);
        byte[] tag56_08Value = tag56_08_StringValue.getBytes();


        //length --> 3
        byte[] tag56_09 = new byte[]{0x09};
        byte[] tag56_09Length = new byte[]{0x01};
        byte[] tag56_09Value = new byte[]{0x01};

        //length --> 5
        String tag56_0A_StringValue = "IST";
        byte[] tag56_0A = new byte[]{0x0A};
        byte[] tag56_0ALength = Utils.integerToByte(tag56_0A_StringValue.length(), 1);
        byte[] tag56_0AValue = tag56_0A_StringValue.getBytes();


        //length --> 3
        byte[] tag56_0B = new byte[]{0x0B};
        byte[] tag56_0BLength = new byte[]{0x01};
        byte[] tag56_0BValue = new byte[]{0x06};


        //length --> 13
        String tag56_0C_StringValue = "MAC ADDRESS";
        byte[] tag56_0C = new byte[]{0x0C};
        byte[] tag56_0CLength = Utils.integerToByte(tag56_0C_StringValue.length(), 1);
        byte[] tag56_0CValue = tag56_0C_StringValue.getBytes();


        //length --> 3
        byte[] tag56_0D = new byte[]{0x0D};
        byte[] tag56_0DLength = new byte[]{0x01};
        byte[] tag56_0DValue = new byte[]{0x04};


        //ds57 length -> 46
        byte[] dataSetID57 = new byte[]{0x57};
        byte[] dataSetLength57 = new byte[]{0x00, 0x2B};

        //length --> 4
        byte[] tag57_01 = new byte[]{0x01};
        byte[] tag57_01Length = new byte[]{0x02};
        byte[] tag57_01Value = new byte[]{0x00, 0x01};

        //length --> 4
        byte[] tag57_02 = new byte[]{0x02};
        byte[] tag57_02Length = new byte[]{0x02};
        byte[] tag57_02Value = new byte[]{0x00, 0x02};

        //length --> 4
        byte[] tag57_03 = new byte[]{0x03};
        byte[] tag57_03Length = new byte[]{0x02};
        byte[] tag57_03Value = new byte[]{0x00, 0x03};

        //length --> 4
        byte[] tag57_04 = new byte[]{0x04};
        byte[] tag57_04Length = new byte[]{0x02};
        byte[] tag57_04Value = new byte[]{0x00, 0x04};

        //length --> 4
        byte[] tag57_05 = new byte[]{0x05};
        byte[] tag57_05Length = new byte[]{0x02};
        byte[] tag57_05Value = new byte[]{0x00, 0x05};


        //length --> 4
        byte[] tag57_06 = new byte[]{0x06};
        byte[] tag57_06Length = new byte[]{0x02};
        byte[] tag57_06Value = new byte[]{0x00, 0x06};


        //length --> 3
        byte[] tag57_07 = new byte[]{0x07};
        byte[] tag57_07Length = new byte[]{0x01};
        byte[] tag57_07Value = new byte[]{0x08};


        //length ---> 4
        String countryCode = "NZ";
        byte[] tag57_08 = new byte[]{0x08};
        byte[] tag57_08Length = new byte[]{0x02};
        byte[] tag57_08Value = countryCode.getBytes();

        //length --> 3
        byte[] tag57_09 = new byte[]{0x09};
        byte[] tag57_09Length = new byte[]{0x01};
        byte[] tag57_09Value = new byte[]{0x09};

        //length --> 3
        byte[] tag57_0A = new byte[]{0x0A};
        byte[] tag57_0ALength = new byte[]{0x01};
        byte[] tag57_0AValue = new byte[]{0x01};


        //length --> 3
        byte[] tag57_0B = new byte[]{0x0B};
        byte[] tag57_0BLength = new byte[]{0x01};
        byte[] tag57_0BValue = new byte[]{0x02};


        //length --> 3
        byte[] tag57_0C = new byte[]{0x0C};
        byte[] tag57_0CLength = new byte[]{0x01};
        byte[] tag57_0CValue = new byte[]{0x03};


        byte[] bytes_56 = Bytes.concat(
                dataSetID56, dataSetLength56,
                tag56_01, tag56_01Length, tag56_01Value,
                tag56_02, tag56_02Length, tag56_02Value,
                tag56_03, tag56_03Length, tag56_03Value,
                tag56_04, tag56_04Length, tag56_04Value,
                tag56_05, tag56_05Length, tag56_05Value,
                tag56_08, tag56_08Length, tag56_08Value,
                tag56_09, tag56_09Length, tag56_09Value,
                tag56_0A, tag56_0ALength, tag56_0AValue,
                tag56_0B, tag56_0BLength, tag56_0BValue,
                tag56_0C, tag56_0CLength, tag56_0CValue,
                tag56_0D, tag56_0DLength, tag56_0DValue);

        byte[] bytes_57 = Bytes.concat(
                dataSetID57, dataSetLength57,
                tag57_01, tag57_01Length, tag57_01Value,
                tag57_02, tag57_02Length, tag57_02Value,
                tag57_03, tag57_03Length, tag57_03Value,
                tag57_04, tag57_04Length, tag57_04Value,
                tag57_05, tag57_05Length, tag57_05Value,
                tag57_06, tag57_06Length, tag57_06Value,
                tag57_07, tag57_07Length, tag57_07Value,
                tag57_08, tag57_08Length, tag57_08Value,
                tag57_09, tag57_09Length, tag57_09Value,
                tag57_0A, tag57_0ALength, tag57_0AValue,
                tag57_0B, tag57_0BLength, tag57_0BValue,
                tag57_0C, tag57_0CLength, tag57_0CValue
        );

        byte[] bytes = Bytes.concat(bytes_56, bytes_57);

        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }

    public byte[] getDE123(AVSType avsType, TransactionType transactionType) {


        //length --> 6
        String tag66_C0_StringValue = "682019";
        byte[] tag66_C0 = new byte[]{(byte) 0xC0};
        byte[] tag66_C0Length = Utils.integerToByte(tag66_C0_StringValue.length(), 1);
        byte[] tag66_C0Value = tag66_C0_StringValue.getBytes();

        //length --> 7
        String tag66_CF_StringValue = "janatha Road vyttila, kochi";
        byte[] tag66_CF = new byte[]{(byte) 0xCF};
        byte[] tag66_CFLength = Utils.integerToByte(tag66_CF_StringValue.length(), 1);
        byte[] tag66_CFValue = tag66_CF_StringValue.getBytes();

        String tag66_D4_StringValue = "Nithin Maloth";
        byte[] tag66_D4 = new byte[]{(byte) 0xD4};
        byte[] tag66_D4Length = Utils.integerToByte(tag66_D4_StringValue.length(), 1);
        byte[] tag66_D4Value = tag66_D4_StringValue.getBytes();


        byte[] dataSet66Value = Bytes.concat(

                tag66_CF, tag66_CFLength, tag66_CFValue,
                tag66_C0, tag66_C0Length, tag66_C0Value,
                tag66_D4, tag66_D4Length, tag66_D4Value
        );


        byte[] dataSetID66 = new byte[]{0x66};
        byte[] dataSetLength66 = Utils.integerToByte(dataSet66Value.length, 2);


        String tag67_03_StringValue = "1";
        byte[] tag67_03 = new byte[]{0x03};
        byte[] tag67_03_value = tag67_03_StringValue.getBytes();
        byte[] tag67_03Length = Utils.integerToByte(tag67_03_value.length, 1);


        String tag67_04_StringValue = "02";
        byte[] tag67_04 = new byte[]{0x04};
        byte[] tag67_04_value = tag67_04_StringValue.getBytes();
        byte[] tag67_04Length = Utils.integerToByte(tag67_04_value.length, 1);

        byte[] tag67_05 = new byte[]{0x05};
        byte[] tag67_05_value = new byte[]{(byte) 0x99, (byte) 0x99, (byte) 0x03, (byte) 0x04};
        byte[] tag67_05Length = Utils.integerToByte(tag67_05_value.length, 1);

        String tag67_07_StringValue = "AB";
        byte[] tag67_07 = new byte[]{0x07};
        byte[] tag67_07_value = tag67_07_StringValue.getBytes();
        byte[] tag67_07Length = Utils.integerToByte(tag67_07_value.length, 1);

        String tag67_08_StringValue = "5";
        byte[] tag67_08 = new byte[]{0x08};
        byte[] tag67_08_value = tag67_08_StringValue.getBytes();
        byte[] tag67_08Length = Utils.integerToByte(tag67_08_value.length, 1);


        byte[] dataSet67Value = Bytes.concat(

                tag67_03, tag67_03Length, tag67_03_value,
                tag67_04, tag67_04Length, tag67_04_value,
                tag67_05, tag67_05Length, tag67_05_value,
                tag67_07, tag67_07Length, tag67_07_value,
                tag67_08, tag67_08Length, tag67_08_value
        );


        byte[] dataSetID67 = new byte[]{0x67};
        byte[] dataSetLength67 = Utils.integerToByte(dataSet67Value.length, 2);


        String tag68_1F31_StringValue = "1234";
        byte[] tag68_1F31 = new byte[]{0x1F, 0x31};
        byte[] tag68_1F31_value = tag68_1F31_StringValue.getBytes();
        byte[] tag68_1F31Length = Utils.integerToByte(tag68_1F31_value.length, 1);


        String tag68_1F32_StringValue = "999";
        byte[] tag68_1F32 = new byte[]{0x1F, 0x32};
        byte[] tag68_1F32_value = tag68_1F32_StringValue.getBytes();
        byte[] tag68_1F32_length = Utils.integerToByte(tag68_1F32_value.length, 1);

        String tag68_1F33_StringValue = "9999999";
        byte[] tag68_1F33 = new byte[]{0x1F, 0x33};
        byte[] tag68_1F33_value = tag68_1F33_StringValue.getBytes();
        byte[] tag68_1F33Length = Utils.integerToByte(tag68_1F33_value.length, 1);

        byte[] tag68_1F35 = new byte[]{0x1F, 0x35};
        byte[] tag68_1F35_value = new byte[]{0x00, 0x05};
        byte[] tag68_1F35Length = Utils.integerToByte(tag68_1F35.length, 1);

        String tag68_01_StringValue = "1234567890123456789";
        byte[] tag68_01 = new byte[]{0x01};
        byte[] tag68_01_value = tag68_01_StringValue.getBytes();
        byte[] tag68_01Length = Utils.integerToByte(tag68_01_value.length, 1);


        String tag68_02_StringValue = "AB";
        byte[] tag68_02 = new byte[]{0x02};
        byte[] tag68_02_value = tag68_02_StringValue.getBytes();
        byte[] tag68_02Length = Utils.integerToByte(tag68_02_value.length, 1);

        String tag68_03_StringValue = "12345678901";
        byte[] tag68_03 = new byte[]{0x03};
        byte[] tag68_03_value = tag68_03_StringValue.getBytes();
        byte[] tag68_03Length = Utils.integerToByte(tag68_03_value.length, 1);

        String tag68_04_StringValue = "123456789012";
        byte[] tag68_04 = new byte[]{0x04};
        byte[] tag68_04_value = tag68_04_StringValue.getBytes();
        byte[] tag68_04Length = Utils.integerToByte(tag68_04_value.length, 1);

//        String tag68_05_StringValue = "12345678901234567890123456789012";
//        byte[] tag68_05 = new byte[]{0x05};
//        byte[] tag68_05_value = tag68_05_StringValue.getBytes();
//        byte[] tag68_05Length = Utils.integerToByte(tag68_05_value.length,1);

        String tag68_06_StringValue = "2112";
        byte[] tag68_06 = new byte[]{0x06};
        byte[] tag68_06_value = tag68_06_StringValue.getBytes();
        byte[] tag68_06Length = Utils.integerToByte(tag68_06_value.length, 1);

        String tag68_07_StringValue = "01";
        byte[] tag68_07 = new byte[]{0x07};
        byte[] tag68_07_value = tag68_07_StringValue.getBytes();
        byte[] tag68_07Length = Utils.integerToByte(tag68_07_value.length, 1);

        String tag68_08_StringValue = "A";
        byte[] tag68_08 = new byte[]{0x08};
        byte[] tag68_08_value = tag68_08_StringValue.getBytes();
        byte[] tag68_08Length = Utils.integerToByte(tag68_08_value.length, 1);


        String tag68_0A_StringValue = "B";
        byte[] tag68_0A = new byte[]{0x0A};
        byte[] tag68_0A_value = tag68_0A_StringValue.getBytes();
        byte[] tag68_0ALength = Utils.integerToByte(tag68_0A_value.length, 1);

        String tag68_0B_StringValue = "1234567890123456789012";
        byte[] tag68_0B = new byte[]{0x0B};
        byte[] tag68_0B_value = tag68_0B_StringValue.getBytes();
        byte[] tag68_0BLength = Utils.integerToByte(tag68_0B_value.length, 1);

        String tag68_1A_StringValue = "123456";
        byte[] tag68_1A = new byte[]{0x1A};
        byte[] tag68_1A_value = tag68_1A_StringValue.getBytes();
        byte[] tag68_1ALength = Utils.integerToByte(tag68_1A_value.length, 1);


        byte[] tag68_1B = new byte[]{0x1B};
        byte[] tag68_1B_value = new byte[]{0x21, 0x11, 0x21, 0x12, 0x45, 0x21};
        byte[] tag68_1BLength = Utils.integerToByte(tag68_1B_value.length, 1);

        byte[] tag68_1C = new byte[]{0x1C};
        byte[] tag68_1C_value = new byte[]{0x00, 0x11};
        byte[] tag68_1CLength = Utils.integerToByte(tag68_1C_value.length, 1);

        byte[] tag68_1D = new byte[]{0x1D};
        byte[] tag68_1D_value = new byte[]{0x00, 0x12};
        byte[] tag68_1DLength = Utils.integerToByte(tag68_1D_value.length, 1);


        String tag68_10_StringValue = "99";
        byte[] tag68_10 = new byte[]{0x10};
        byte[] tag68_10_value = tag68_10_StringValue.getBytes();
        byte[] tag68_10Length = Utils.integerToByte(tag68_10_value.length, 1);

        String tag68_11_StringValue = "85";
        byte[] tag68_11 = new byte[]{0x11};
        byte[] tag68_11_value = tag68_11_StringValue.getBytes();
        byte[] tag68_11Length = Utils.integerToByte(tag68_11_value.length, 1);

        String tag68_12_StringValue = "12";
        byte[] tag68_12 = new byte[]{0x12};
        byte[] tag68_12_value = tag68_12_StringValue.getBytes();
        byte[] tag68_12Length = Utils.integerToByte(tag68_12_value.length, 1);

        String tag68_13_StringValue = "25";
        byte[] tag68_13 = new byte[]{0x13};
        byte[] tag68_13_value = tag68_13_StringValue.getBytes();
        byte[] tag68_13Length = Utils.integerToByte(tag68_13_value.length, 1);


        String tag68_14_StringValue = "88";
        byte[] tag68_14 = new byte[]{0x14};
        byte[] tag68_14_value = tag68_14_StringValue.getBytes();
        byte[] tag68_14Length = Utils.integerToByte(tag68_14_value.length, 1);


        byte[] dataSet68Value = Bytes.concat(

                tag68_1F31, tag68_1F31Length, tag68_1F31_value,
                tag68_1F32, tag68_1F32_length, tag68_1F32_value,
                tag68_1F33, tag68_1F33Length, tag68_1F33_value,
                tag68_1F35, tag68_1F35Length, tag68_1F35_value,
                tag68_01, tag68_01Length, tag68_01_value,
                tag68_02, tag68_02Length, tag68_02_value,
                tag68_03, tag68_03Length, tag68_03_value,
                tag68_04, tag68_04Length, tag68_04_value,
//                tag68_05,tag68_05Length,tag68_05_value,
                tag68_06, tag68_06Length, tag68_06_value,
                tag68_06, tag68_06Length, tag68_06_value,
                tag68_07, tag68_07Length, tag68_07_value,
                tag68_08, tag68_08Length, tag68_08_value,
                tag68_0A, tag68_0ALength, tag68_0A_value,
                tag68_0B, tag68_0BLength, tag68_0B_value,
                tag68_1A, tag68_1ALength, tag68_1A_value,
                tag68_1B, tag68_1BLength, tag68_1B_value,
                tag68_1C, tag68_1CLength, tag68_1C_value,
                tag68_1D, tag68_1DLength, tag68_1D_value,
                tag68_10, tag68_10Length, tag68_10_value,
                tag68_11, tag68_11Length, tag68_11_value,
                tag68_12, tag68_12Length, tag68_12_value,
                tag68_13, tag68_13Length, tag68_13_value,
                tag68_14, tag68_14Length, tag68_14_value
        );


        byte[] dataSetID68 = new byte[]{0x68};
        byte[] dataSetLength68 = Utils.integerToByte(dataSet68Value.length, 2);


        byte[] bytes_66 = Bytes.concat(
                dataSetID66, dataSetLength66, dataSet66Value);

        byte[] bytes_67 = Bytes.concat(
                dataSetID67, dataSetLength67, dataSet67Value);

        byte[] bytes_68 = Bytes.concat(
                dataSetID68, dataSetLength68, dataSet68Value);

        byte[] bytes = null;

        if (avsType.equals(AVSType.AVS)) {
            bytes = bytes_66;
        }

        switch (transactionType) {

            case TOKEN_ELIGIBILITY:
            case TOKEN_ACTIVATION_REQUEST:
            case TOKEN_COMPLETION_NOTIFICATION:
            case TOKEN_EVENT_NOTIFICATION: {

                if (bytes != null) {
                    bytes = Bytes.concat(bytes, bytes_67, bytes_68);
                } else {
                    bytes = Bytes.concat(bytes_67, bytes_68);
                }
            }

        }

        if (bytes != null) {
            return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);
        } else {
            throw new RuntimeException("Invalid AVS Type and Transaction Type");
        }


    }


    public byte[] getDE125() {


        String tag01_01_StringValue = "04";
        byte[] tag01_01 = new byte[]{(byte) 0x01};
        byte[] tag01_01Length = Utils.integerToByte(tag01_01_StringValue.length(), 1);
        byte[] tag01_01Value = tag01_01_StringValue.getBytes();


        String tag01_02_StringValue = "ENG";
        byte[] tag01_02 = new byte[]{(byte) 0x02};
        byte[] tag01_02Length = Utils.integerToByte(tag01_02_StringValue.length(), 1);
        byte[] tag01_02Value = tag01_02_StringValue.getBytes();

        String tag01_03_StringValue = "Test 123";
        byte[] tag01_03 = new byte[]{(byte) 0x03};
        byte[] tag01_03Length = Utils.integerToByte(tag01_03_StringValue.length(), 1);
        byte[] tag01_03Value = tag01_03_StringValue.getBytes();

        String tag01_04_StringValue = "123456789012345";
        byte[] tag01_04 = new byte[]{(byte) 0x04};
        byte[] tag01_04Length = Utils.integerToByte(tag01_04_StringValue.length(), 1);
        byte[] tag01_04Value = tag01_04_StringValue.getBytes();

        String tag01_05_StringValue = "Device name";
        byte[] tag01_05 = new byte[]{(byte) 0x05};
        byte[] tag01_05Length = Utils.integerToByte(tag01_05_StringValue.length(), 1);
        byte[] tag01_05Value = tag01_05_StringValue.getBytes();

        String tag01_06_StringValue = "Location ";
        byte[] tag01_06 = new byte[]{(byte) 0x06};
        byte[] tag01_06Length = Utils.integerToByte(tag01_06_StringValue.length(), 1);
        byte[] tag01_06Value = tag01_06_StringValue.getBytes();

        String tag01_07_StringValue = "128.0.0.1 ";
        byte[] tag01_07 = new byte[]{(byte) 0x07};
        byte[] tag01_07Length = Utils.integerToByte(tag01_07_StringValue.length(), 1);
        byte[] tag01_07Value = tag01_07_StringValue.getBytes();


        byte[] dataSet01Value = Bytes.concat(

                tag01_01, tag01_01Length, tag01_01Value,
                tag01_02, tag01_02Length, tag01_02Value,
                tag01_03, tag01_03Length, tag01_03Value,
                tag01_04, tag01_04Length, tag01_04Value,
                tag01_05, tag01_05Length, tag01_05Value,
                tag01_06, tag01_06Length, tag01_06Value,
                tag01_07, tag01_07Length, tag01_07Value

        );


        byte[] dataSetID01 = new byte[]{0x01};
        byte[] dataSetLength01 = Utils.integerToByte(dataSet01Value.length, 2);

        String tag02_03_StringValue = "1";
        byte[] tag02_03 = new byte[]{(byte) 0x03};
        byte[] tag02_03Length = Utils.integerToByte(tag02_03_StringValue.length(), 1);
        byte[] tag02_03Value = tag02_03_StringValue.getBytes();

        String tag02_04_StringValue = "ABCD";
        byte[] tag02_04 = new byte[]{(byte) 0x04};
        byte[] tag02_04Length = Utils.integerToByte(tag02_04_StringValue.length(), 1);
        byte[] tag02_04Value = tag02_04_StringValue.getBytes();


        String tag02_05_StringValue = "01";
        byte[] tag02_05 = new byte[]{(byte) 0x05};
        byte[] tag02_05Length = Utils.integerToByte(tag02_05_StringValue.length(), 1);
        byte[] tag02_05Value = tag02_05_StringValue.getBytes();

        String tag02_06_StringValue = "02";
        byte[] tag02_06 = new byte[]{(byte) 0x06};
        byte[] tag02_06Length = Utils.integerToByte(tag02_06_StringValue.length(), 1);
        byte[] tag02_06Value = tag02_06_StringValue.getBytes();

        String tag02_07_StringValue = "0203040A";
        byte[] tag02_07 = new byte[]{(byte) 0x07};
        byte[] tag02_07Length = Utils.integerToByte(tag02_07_StringValue.length(), 1);
        byte[] tag02_07Value = tag02_07_StringValue.getBytes();

        String tag02_08_StringValue = "01";
        byte[] tag02_08 = new byte[]{(byte) 0x08};
        byte[] tag02_08Length = Utils.integerToByte(tag02_08_StringValue.length(), 1);
        byte[] tag02_08Value = tag02_08_StringValue.getBytes();

        String tag02_09_StringValue = "SAMSUNG PAY";
        byte[] tag02_09 = new byte[]{(byte) 0x09};
        byte[] tag02_09Length = Utils.integerToByte(tag02_09_StringValue.length(), 1);
        byte[] tag02_09Value = tag02_09_StringValue.getBytes();

        String tag02_0A_StringValue = "samsung.com";
        byte[] tag02_0A = new byte[]{(byte) 0x0A};
        byte[] tag02_0ALength = Utils.integerToByte(tag02_0A_StringValue.length(), 1);
        byte[] tag02_0AValue = tag02_0A_StringValue.getBytes();

        byte[] dataSet02Value = Bytes.concat(

                tag02_03, tag02_03Length, tag02_03Value,
                tag02_04, tag02_04Length, tag02_04Value,
                tag02_05, tag02_05Length, tag02_05Value,
                tag02_06, tag02_06Length, tag02_06Value,
                tag02_07, tag02_07Length, tag02_07Value,
                tag02_08, tag02_08Length, tag02_08Value,
                tag02_09, tag02_09Length, tag02_09Value,
                tag02_0A, tag02_0ALength, tag02_0AValue

        );


        byte[] dataSetID02 = new byte[]{0x02};
        byte[] dataSetLength02 = Utils.integerToByte(dataSet02Value.length, 2);


        byte[] tag03_03 = new byte[]{(byte) 0x03};
        byte[] tag03_03Value = new byte[]{0x01, 0x23, 0x45, 0x67, (byte) 0x89, 0x01, 0x23, 0x45};
        byte[] tag03_03Length = Utils.integerToByte(tag03_03Value.length, 1);

        byte[] dataSet03Value = Bytes.concat(

                tag03_03, tag03_03Length, tag03_03Value
        );


        byte[] dataSetID03 = new byte[]{0x03};
        byte[] dataSetLength03 = Utils.integerToByte(dataSet03Value.length, 2);


        byte[] bytes_01 = Bytes.concat(
                dataSetID01, dataSetLength01, dataSet01Value);

        byte[] bytes_02 = Bytes.concat(
                dataSetID02, dataSetLength02, dataSet02Value);

        byte[] bytes_03 = Bytes.concat(
                dataSetID03, dataSetLength03, dataSet03Value);


        byte[] bytes = Bytes.concat(bytes_01, bytes_02, bytes_03);
        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }


    public byte[] getDE126() {

        BitSet bitSet = new BitSet(8);
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(6);
        bitSet.set(7);
        bitSet.set(8);
        bitSet.set(9);
        bitSet.set(11);
        bitSet.set(12);
        bitSet.set(17);
        bitSet.set(18);
        bitSet.set(19);


        byte[] de005 = "ABCDEFGH".getBytes();
        byte[] de006 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        byte[] de007 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        ;
        byte[] de008 = "B".repeat(20).getBytes();
        byte[] de009 = "A".repeat(20).getBytes();
        byte[] de010 = "10123 ".getBytes();
        byte[] de012 = new byte[]{(byte) 0xF8, 0x00, 0x00};
        byte[] de013 = "R".getBytes();
        byte[] de018 = "C".repeat(12).getBytes();
        byte[] de019 = "1".getBytes();
        byte[] de020 = "2".getBytes();

        byte[] bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de005, de006, de007, de008, de009, de010,
                de012, de013, de018, de019, de020);


        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }


    public byte[] getDE126(RecurringTrans recurringTrans, InstallmentType installmentType) {

        BitSet bitSet = new BitSet(8);
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(6);
        bitSet.set(7);
        bitSet.set(8);
        bitSet.set(9);
        bitSet.set(11);
        bitSet.set(12);
        bitSet.set(17);
        bitSet.set(18);
        bitSet.set(19);


        byte[] de005 = "ABCDEFGH".getBytes();
        byte[] de006 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        byte[] de007 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        ;
        byte[] de008 = "B".repeat(20).getBytes();
        byte[] de009 = "A".repeat(20).getBytes();
        byte[] de010 = "10123 ".getBytes();
        byte[] de012 = new byte[]{(byte) 0xF8, 0x00, 0x00};

        byte[] de013;
        if (recurringTrans.equals(RecurringTrans.RECURRING_TRANS)) {
            de013 = "R".getBytes();

        } else {

            if (installmentType.equals(InstallmentType.INSTALLMENT_TYPE)) {
                de013 = "I".getBytes();
            } else {
                de013 = "A".getBytes();
            }

        }
        byte[] de018 = "C".repeat(12).getBytes();
        byte[] de019 = "1".getBytes();
        byte[] de020 = "2".getBytes();

        byte[] bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de005, de006, de007, de008, de009, de010,
                de012, de013, de018, de019, de020);


        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }

    public byte[] getDE126(RecurringTrans recurringTrans) {

        BitSet bitSet = new BitSet(8);
        bitSet.set(4);
        bitSet.set(5);
        bitSet.set(6);
        bitSet.set(7);
        bitSet.set(8);
        bitSet.set(9);
        bitSet.set(11);
        bitSet.set(12);
        bitSet.set(17);
        bitSet.set(18);
        bitSet.set(19);


        byte[] de005 = "ABCDEFGH".getBytes();
        byte[] de006 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        byte[] de007 = new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
        ;
        byte[] de008 = "B".repeat(20).getBytes();
        byte[] de009 = "A".repeat(20).getBytes();
        byte[] de010 = "10123 ".getBytes();
        byte[] de012 = new byte[]{(byte) 0xF8, 0x00, 0x00};

        byte[] de013;
        if (recurringTrans.equals(RecurringTrans.RECURRING_TRANS)) {
            de013 = "R".getBytes();

        } else {


            de013 = "A".getBytes();


        }
        byte[] de018 = "C".repeat(12).getBytes();
        byte[] de019 = "1".getBytes();
        byte[] de020 = "2".getBytes();

        byte[] bytes = Bytes.concat(Utils.createByteArrayFromBitSet(bitSet, 8), de005, de006, de007, de008, de009, de010,
                de012, de013, de018, de019, de020);


        return Bytes.concat(Utils.integerToByte(bytes.length, 1), bytes);


    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification, String txnCurrencyCode, String billingCurrencyCode) {

        switch (integer) {
            case 2: {
                return getDE002();
            }
            case 3: {
                return getDE003(transactionType);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018();
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039();
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060();
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062();
            }
            case 63: {
                return getDE063();
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126();
            }
            default: {
                return null;
            }

        }

    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification, String txnCurrencyCode, String billingCurrencyCode, int testNumber) {

        switch (integer) {
            case 2: {
                return getDE002();
            }
            case 3: {
                return getDE003(transactionType);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018();
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039(testNumber);
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060();
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062(testNumber);
            }
            case 63: {
                return getDE063();
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126();
            }
            default: {
                return null;
            }

        }

    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification, String txnCurrencyCode, String billingCurrencyCode, int testNumber,String instrument) {

        switch (integer) {
            case 2: {
                return getDE002(instrument);
            }
            case 3: {
                return getDE003(transactionType);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018();
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039(testNumber);
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060();
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062(testNumber);
            }
            case 63: {
                return getDE063();
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126();
            }
            default: {
                return null;
            }

        }

    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification, String txnCurrencyCode, String billingCurrencyCode, String fromAccount) {

        switch (integer) {
            case 2: {
                return getDE002();
            }
            case 3: {
                return getDE003(transactionType, fromAccount);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018();
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039();
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060();
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062();
            }
            case 63: {
                return getDE063();
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126();
            }
            default: {
                return null;
            }

        }

    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification,
                           TerminalType terminalType, AuthorizationType authorizationType,
                           String txnCurrencyCode, String billingCurrencyCode

    ) {

        switch (integer) {
            case 2: {
                return getDE002();
            }
            case 3: {
                return getDE003(transactionType);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018(transactionType, terminalType);
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification, entryMode);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039();
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060(verification, entryMode);
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062(authorizationType);
            }
            case 63: {
                return getDE063(transactionType, authorizationType);
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126(recurringTrans);
            }
            default: {
                return null;
            }

        }

    }

    private byte[] getData(Integer integer,
                           TransactionType transactionType,
                           TerminalPinCapability terminalPinCapability,
                           EntryMode entryMode, AVSType avsType,
                           RecurringTrans recurringTrans,
                           PinEntryMode pinEntryMode, String verification,
                           TerminalType terminalType, AuthorizationType authorizationType, InstallmentType installmentType,
                           String txnCurrencyCode, String billingCurrencyCode

    ) {

        switch (integer) {
            case 2: {
                return getDE002();
            }
            case 3: {
                return getDE003(transactionType);
            }
            case 4: {
                return getDE004();
            }
            case 6: {
                return getDE006();
            }
            case 7: {
                return getDE007();
            }
            case 10: {
                return getDE010();
            }
            case 11: {
                return getDE011();
            }
            case 12: {
                return getDE012();
            }
            case 13: {
                return getDE013();
            }
            case 14: {
                return getDE014();
            }
            case 18: {
                return getDE018(transactionType, terminalType);
            }
            case 19: {
                return getDE019();
            }
            case 20: {
                return getDE020();
            }
            case 22: {
                return getDE022(entryMode, terminalPinCapability);
            }
            case 23: {
                return getDE023();
            }
            case 25: {
                return getDE025(verification, entryMode);
            }
            case 26: {
                return getDE026();
            }
            case 28: {
                return getDE028();
            }
            case 32: {
                return getDE032();
            }
            case 33: {
                return getDE033();
            }
            case 35: {
                return getDE035();
            }
            case 37: {
                return getDE037();
            }
            case 38: {
                return getDE038();
            }
            case 39: {
                return getDE039();
            }
            case 41: {
                return getDE041();
            }
            case 42: {
                return getDE042();
            }
            case 43: {
                return getDE043();
            }
            case 44: {
                return getDE044();
            }
            case 45: {
                return getDE045();
            }
            case 48: {
                return getDE048();
            }
            case 49: {
                return getDE049(txnCurrencyCode);
            }
            case 51: {
                return getDE051(billingCurrencyCode);
            }
            case 52: {
                return getDE052();
            }
            case 53: {
                return getDE053();
            }
            case 54: {
                return getDE054();
            }
            case 55: {
                return getDE055();
            }
            case 56: {
                return getDE056();
            }
            case 59: {
                return getDE059();
            }
            case 60: {
                return getDE060(verification, entryMode, terminalType);
            }
            case 61: {
                return getDE061();
            }
            case 62: {
                return getDE062(authorizationType);
            }
            case 63: {
                return getDE063(transactionType, authorizationType);
            }
            case 68: {
                return getDE068();
            }
            case 70: {
                return getDE070();
            }
            case 73: {
                return getDE073();
            }
            case 90: {
                return getDE090();
            }
            case 91: {
                return getDE091();
            }
            case 92: {
                return getDE092();
            }
            case 95: {
                return getDE095();
            }
            case 100: {
                return getDE100();
            }
            case 101: {
                return getDE101();
            }
            case 102: {
                return getDE102();
            }
            case 103: {
                return getDE103();
            }
            case 115: {
                return getDE115();
            }
            case 117: {
                return getDE117();
            }
            case 118: {
                return getDE118();
            }
            case 120: {
                return getDE120();
            }
            case 121: {
                return getDE121();
            }
            case 123: {
                return getDE123(avsType, transactionType);
            }
            case 125: {
                return getDE125();
            }
            case 126: {
                return getDE126(recurringTrans, installmentType);
            }
            default: {
                return null;
            }

        }

    }


}
