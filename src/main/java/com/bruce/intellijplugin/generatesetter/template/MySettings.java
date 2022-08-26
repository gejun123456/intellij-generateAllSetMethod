/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

package com.bruce.intellijplugin.generatesetter.template;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.rits.cloning.Cloner;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author bruce ge 2022/8/25
 */
public class MySettings implements Configurable {


    @Override
    public String getDisplayName() {
        return "GenerateAllSetter";
    }

    private GenerateAllSetterSettingForm generateAllSetterSettingForm;

    @Nullable
    @Override
    public JComponent createComponent() {
        generateAllSetterSettingForm = new GenerateAllSetterSettingForm();
        GenerateSetterService instance = GenerateSetterService.getInstance();
        GenerateSetterState state = instance.getState();
        GenerateSetterState generateSetterState = Cloner.standard().deepClone(state);
        generateAllSetterSettingForm.importFromSettings(generateSetterState);
        JPanel thePanel = generateAllSetterSettingForm.getThePanel();
        return thePanel;
    }

    @Override
    public boolean isModified() {
        GenerateSetterState theState = generateAllSetterSettingForm.getTheState();
        GenerateSetterService instance = GenerateSetterService.getInstance();
        GenerateSetterState state = instance.getState();
        if(theState.equals(state)){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void reset() {
        GenerateSetterService instance = GenerateSetterService.getInstance();
        GenerateSetterState state = instance.getState();
        GenerateSetterState generateSetterState = Cloner.standard().deepClone(state);
        generateAllSetterSettingForm.importFromSettings(generateSetterState);
    }

    @Override
    public void apply() throws ConfigurationException {
        GenerateSetterState theState = generateAllSetterSettingForm.getTheState();
        GenerateSetterState generateSetterState = Cloner.standard().deepClone(theState);
        GenerateSetterService instance = GenerateSetterService.getInstance();
        instance.loadState(generateSetterState);
    }
}
