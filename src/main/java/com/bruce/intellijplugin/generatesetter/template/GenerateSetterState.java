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
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * @author bruce ge 2022/8/25
 */
public class GenerateSetterState {
    private Boolean useJdkClassesOnly = false;
    private Boolean generateByTemplate = false;

    private TestEngine preferredTestEngine = TestEngine.ASSERTJ;

    private List<Template> templateList = Lists.newArrayList();

    public Boolean getUseJdkClassesOnly() {
        return useJdkClassesOnly;
    }

    public void setUseJdkClassesOnly(Boolean useJdkClassesOnly) {
        this.useJdkClassesOnly = useJdkClassesOnly;
    }

    public Boolean getGenerateByTemplate() {
        return generateByTemplate;
    }

    public void setGenerateByTemplate(Boolean generateByTemplate) {
        this.generateByTemplate = generateByTemplate;
    }

    public List<Template> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<Template> templateList) {
        this.templateList = templateList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GenerateSetterState that = (GenerateSetterState) o;

        return new EqualsBuilder()
                .append(useJdkClassesOnly, that.useJdkClassesOnly)
                .append(generateByTemplate, that.generateByTemplate)
                .append(templateList, that.templateList)
                .append(preferredTestEngine, that.preferredTestEngine)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(useJdkClassesOnly)
                .append(generateByTemplate)
                .append(templateList)
                .append(preferredTestEngine)
                .toHashCode();
    }

    public TestEngine getPreferredTestEngine() {
        return preferredTestEngine;
    }

    public void setPreferredTestEngine(TestEngine preferredTestEngine) {
        this.preferredTestEngine = preferredTestEngine;
    }
}
