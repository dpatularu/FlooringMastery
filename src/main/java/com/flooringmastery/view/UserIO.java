package com.flooringmastery.view;

import java.math.BigDecimal;

public interface UserIO {
    void println(String msg);

    void print(String s);
    String readString(String prompt);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    BigDecimal readBigDecimal(String prompt);

    BigDecimal readBigDecimal(String prompt, InputMode inputMode);
    BigDecimal readBigDecimal(String prompt, BigDecimal min);

    BigDecimal readBigDecimal(String prompt, BigDecimal min, InputMode inputMode);

}