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

import com.bruce.intellijplugin.generatesetter.TestEngine;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author bruce ge 2022/8/25
 */
@State(
        name = "com.bruce.GenerateAllSetterState",
        storages = @Storage("GenerateAllSetterPlugin.xml"))
public class GenerateSetterService implements PersistentStateComponent<GenerateSetterState> {

    public static GenerateSetterService getInstance() {
        return ServiceManager.getService(GenerateSetterService.class);
    }

    private GenerateSetterState myState = new GenerateSetterState();

    @Nullable
    @Override
    public GenerateSetterState getState() {
        GenerateSetterState generateSetterState = new GenerateSetterState();
        generateSetterState.setPreferredTestEngine(PreferredTestEngineProvider.getTestEngine());
        return generateSetterState;
    }

    @Override
    public void loadState(@NotNull GenerateSetterState state) {
        GenerateSetterState generateSetterState = new GenerateSetterState();
        generateSetterState.setPreferredTestEngine(TestEngine.JUNIT4);
        myState = generateSetterState;
    }
}
