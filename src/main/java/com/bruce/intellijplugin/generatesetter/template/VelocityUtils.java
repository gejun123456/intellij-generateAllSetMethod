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

import com.intellij.psi.PsiClass;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author bruce ge 2022/9/13
 */
public class VelocityUtils {

    public static String generate(String text, PsiClass aClass, String name) {
        VelocityEngine velocityEngine = new VelocityEngine();
        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("theClass", aClass);
        velocityContext.put("variableName", name);
        StringWriter stringWriter = new StringWriter();
        try {
            velocityEngine.evaluate(velocityContext, stringWriter, "Velocity Code Generate", text);
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder("catch exception when generate\n");
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            builder.append(writer.toString());
            return builder.toString().replace("\r", "");
        }
        String code = stringWriter.toString();
        return code.toString();
    }
}
