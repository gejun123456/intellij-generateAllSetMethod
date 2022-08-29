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

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author bruce ge 2022/8/29
 */
public class GenerateTemplateTest {
    private static Map<String, String> typeGeneratedMap = new HashMap<String, String>() {
        {
            put("boolean", "false");
            put("java.lang.Boolean", "false");
            put("int", "0");
            put("byte", "(byte)0");
            put("java.lang.Byte", "(byte)0");
            put("java.lang.Integer", "0");
            put("java.lang.String", "\"\"");
            put("java.math.BigDecimal", "new BigDecimal(\"0\")");
            put("java.lang.Long", "0L");
            put("long", "0L");
            put("short", "(short)0");
            put("java.lang.Short", "(short)0");
            put("java.util.Date", "new Date()");
            put("float", "0.0F");
            put("java.lang.Float", "0.0F");
            put("double", "0.0D");
            put("java.lang.Double", "0.0D");
            put("java.lang.Character", "\'\'");
            put("char", "\'\'");
            put("java.time.LocalDateTime", "LocalDateTime.now()");
            put("java.time.LocalDate", "LocalDate.now()");

        }
    };
    @Test
    public void testGenerateTemplate(){
        StringBuilder builder = new StringBuilder();
        typeGeneratedMap.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                builder.append(" #elseif($parameterType.equals(\""+s+"\"))");
                builder.append("\n");
                builder.append("#set($defaultValue=\""+escape(s2)+"\")");
                builder.append("\n");
            }
        });
        System.out.println(builder.toString());
    }

    private String escape(String s2) {
        return s2.replace("\"","\"\"");
    }
}
