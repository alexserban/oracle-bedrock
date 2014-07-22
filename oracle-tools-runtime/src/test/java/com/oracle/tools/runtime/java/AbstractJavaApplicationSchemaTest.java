/*
 * File: AbstractJavaApplicationSchemaTest.java
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the terms and conditions of 
 * the Common Development and Distribution License 1.0 (the "License").
 *
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License by consulting the LICENSE.txt file
 * distributed with this file, or by consulting https://oss.oracle.com/licenses/CDDL
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file LICENSE.txt.
 *
 * MODIFICATIONS:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 */

package com.oracle.tools.runtime.java;

import com.oracle.tools.runtime.ApplicationConsole;
import com.oracle.tools.runtime.Platform;
import com.oracle.tools.runtime.PlatformAware;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AbstractJavaApplicationSchema}.
 * <p>
 * Copyright (c) 2014. All Rights Reserved. Oracle Corporation.<br>
 * Oracle is a registered trademark of Oracle Corporation and/or its affiliates.
 *
 * @author Jonathan Knight
 */
public class AbstractJavaApplicationSchemaTest
{
    /**
     * Test that remote debug mode is set to the value returned
     * from {@link JavaVirtualMachine#shouldEnabledRemoteDebug()}
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldDefaultDebugModeToFalseFromJVM() throws Exception
    {
        JavaVirtualMachine jvm = JavaVirtualMachine.getInstance();

        when(jvm.shouldEnabledRemoteDebug()).thenReturn(false);

        AbstractJavaApplicationSchema schema = new AbstractJavaApplicationSchemaStub(SimpleJavaApplication.class,
                                                                                     "TestExe",
                                                                                     "TestApp",
                                                                                     "classpath");

        assertThat(schema.isRemoteDebuggingEnabled(), is(false));
        verify(jvm, atLeastOnce()).shouldEnabledRemoteDebug();
    }


    /**
     * Test that remote debug mode is set to the value returned
     * from {@link JavaVirtualMachine#shouldEnabledRemoteDebug()}
     */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldDefaultDebugModeToTrueFromJVM() throws Exception
    {
        JavaVirtualMachine jvm = JavaVirtualMachine.getInstance();

        when(jvm.shouldEnabledRemoteDebug()).thenReturn(true);

        AbstractJavaApplicationSchema schema = new AbstractJavaApplicationSchemaStub(SimpleJavaApplication.class,
                                                                                     "TestExe",
                                                                                     "TestApp",
                                                                                     "classpath");

        assertThat(schema.isRemoteDebuggingEnabled(), is(true));
        verify(jvm, atLeastOnce()).shouldEnabledRemoteDebug();
    }


    @Test
    @SuppressWarnings("unchecked")
    public void shouldUsePlatformAwarePropertyValue() throws Exception
    {
        AbstractJavaApplicationSchema schema = new AbstractJavaApplicationSchemaStub(SimpleJavaApplication.class,
                                                                                     "TestExe",
                                                                                     "TestApp",
                                                                                     "classpath");

        Platform      platform = mock(Platform.class);
        PlatformAware property = mock(PlatformAware.class);

        when(property.toString()).thenReturn("my-property-value");

        schema.setSystemProperty("my.property", property);

        Properties properties = schema.getSystemProperties(platform);
        assertThat(properties.getProperty("my.property"), is("my-property-value"));

        verify(property).setPlatform(same(platform));
    }


    /**
     * Mock out the {@link JavaVirtualMachine#INSTANCE} so we can
     * mock its methods
     */
    @Before
    public void mockJavaVirtualMachine() throws Exception
    {
        JavaVirtualMachineMockHelper.mockJavaVirtualMachine();
    }


    /**
     * Restore the {@link JavaVirtualMachine#INSTANCE}
     */
    @After
    public void restoreJavaVirtualMachine() throws Exception
    {
        JavaVirtualMachineMockHelper.restoreJavaVirtualMachine();
    }


    /**
     * A stub of {@link AbstractJavaApplicationSchema} for testing.
     */
    public static class AbstractJavaApplicationSchemaStub<A extends JavaApplication,
                                                          S extends AbstractJavaApplicationSchema<A, S>>
        extends AbstractJavaApplicationSchema<A, S>
    {
        private Class<A> applicationType;

        public AbstractJavaApplicationSchemaStub(Class<A> applicationType,
                                                 String   executableName,
                                                 String   applicationClassName,
                                                 String   classPath)
        {
            super(executableName, applicationClassName, classPath);
            this.applicationType = applicationType;
        }


        @Override
        protected void configureDefaults()
        {
        }


        @Override
        public A createJavaApplication(JavaProcess process,
                                       String name,
                                       Platform platform, ApplicationConsole console,
                                       Properties environmentVariables,
                                       Properties systemProperties, int remoteDebuggingPort)
        {
            return null;
        }

        @Override
        public Class<A> getApplicationClass()
        {
            return applicationType;
        }
    }
}