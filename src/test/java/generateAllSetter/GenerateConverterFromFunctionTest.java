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

package generateAllSetter;

import com.bruce.intellijplugin.generatesetter.CommonConstants;
import com.intellij.codeInsight.intention.IntentionAction;
import org.assertj.core.api.Assertions;

/**
 * @author bruce ge
 */
public class GenerateConverterFromFunctionTest extends BaseTest {
    public void testConverter() {
        myFixture.configureByText("Article.java","package com;\n" +
                "import java.math.BigDecimal;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "/**\n" +
                " * Created by bruce.ge on 2017/2/3.\n" +
                " */\n" +
                "public class Article {\n" +
                "    private Integer id;\n" +
                "\n" +
                "    private String uuuuuuu;\n" +
                "\n" +
                "    private Long length;\n" +
                "\n" +
                "    private Date createTime;\n" +
                "\n" +
                "    private Date updateTime;\n" +
                "\n" +
                "    private Boolean hasMore;\n" +
                "\n" +
                "    private BigDecimal priority;\n" +
                "\n" +
                "    public Integer getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "\n" +
                "    public void setId(Integer id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "\n" +
                "    public String getUuuuuuu() {\n" +
                "        return uuuuuuu;\n" +
                "    }\n" +
                "\n" +
                "    public void setUuuuuuu(String uuuuuuu) {\n" +
                "        this.uuuuuuu = uuuuuuu;\n" +
                "    }\n" +
                "\n" +
                "    public Long getLength() {\n" +
                "        return length;\n" +
                "    }\n" +
                "\n" +
                "    public void setLength(Long length) {\n" +
                "        this.length = length;\n" +
                "    }\n" +
                "\n" +
                "    public Date getCreateTime() {\n" +
                "        return createTime;\n" +
                "    }\n" +
                "\n" +
                "    public void setCreateTime(Date createTime) {\n" +
                "        this.createTime = createTime;\n" +
                "    }\n" +
                "\n" +
                "    public Date getUpdateTime() {\n" +
                "        return updateTime;\n" +
                "    }\n" +
                "\n" +
                "    public void setUpdateTime(Date updateTime) {\n" +
                "        this.updateTime = updateTime;\n" +
                "    }\n" +
                "\n" +
                "    public Boolean getHasMore() {\n" +
                "        return hasMore;\n" +
                "    }\n" +
                "\n" +
                "    public void setHasMore(Boolean hasMore) {\n" +
                "        this.hasMore = hasMore;\n" +
                "    }\n" +
                "\n" +
                "    public BigDecimal getPriority() {\n" +
                "        return priority;\n" +
                "    }\n" +
                "\n" +
                "    public void setPriority(BigDecimal priority) {\n" +
                "        this.priority = priority;\n" +
                "    }\n" +
                "\n" +
                "    public static Article conv<caret>ertFrom(User user){\n" +
                "        \n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class User{\n" +
                "    private String uuuuuuu;\n" +
                "\n" +
                "    private Long length;\n" +
                "\n" +
                "    private Date createTime;\n" +
                "\n" +
                "    private Date updateTime;\n" +
                "\n" +
                "    private Boolean hasMore;\n" +
                "\n" +
                "    public String getUuuuuuu() {\n" +
                "        return uuuuuuu;\n" +
                "    }\n" +
                "\n" +
                "    public void setUuuuuuu(String uuuuuuu) {\n" +
                "        this.uuuuuuu = uuuuuuu;\n" +
                "    }\n" +
                "\n" +
                "    public Long getLength() {\n" +
                "        return length;\n" +
                "    }\n" +
                "\n" +
                "    public void setLength(Long length) {\n" +
                "        this.length = length;\n" +
                "    }\n" +
                "\n" +
                "    public Date getCreateTime() {\n" +
                "        return createTime;\n" +
                "    }\n" +
                "\n" +
                "    public void setCreateTime(Date createTime) {\n" +
                "        this.createTime = createTime;\n" +
                "    }\n" +
                "\n" +
                "    public Date getUpdateTime() {\n" +
                "        return updateTime;\n" +
                "    }\n" +
                "\n" +
                "    public void setUpdateTime(Date updateTime) {\n" +
                "        this.updateTime = updateTime;\n" +
                "    }\n" +
                "\n" +
                "    public Boolean getHasMore() {\n" +
                "        return hasMore;\n" +
                "    }\n" +
                "\n" +
                "    public void setHasMore(Boolean hasMore) {\n" +
                "        this.hasMore = hasMore;\n" +
                "    }\n" +
                "}\n");

        IntentionAction singleIntention = myFixture.findSingleIntention(CommonConstants.GENERATE_CONVERTER_FROM_METHOD);

        Assertions.assertThat(singleIntention).isNotNull();
        myFixture.launchAction(singleIntention);

        myFixture.checkResultByFile("after/GenerateConverterFromFunction.java",true);
    }
}
