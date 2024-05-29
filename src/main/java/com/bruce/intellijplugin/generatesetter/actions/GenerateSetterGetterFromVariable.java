package com.bruce.intellijplugin.generatesetter.actions;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.bruce.intellijplugin.generatesetter.GenerateAllHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class GenerateSetterGetterFromVariable extends GenerateAllSetterBase {

    public GenerateSetterGetterFromVariable() {
        super(new GenerateAllHandlerAdapter() {
            @Override
            public boolean isSetterFromVariable() {
                return true;
            }
        });
    }

    @NotNull
    @Override
    public String getText() {
        return CommonConstants.GENERATE_CONVERTER_FROM_VARIABLE;
    }
}
