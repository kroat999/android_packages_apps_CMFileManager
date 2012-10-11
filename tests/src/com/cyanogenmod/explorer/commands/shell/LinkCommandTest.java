/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.explorer.commands.shell;

import com.cyanogenmod.explorer.console.InsufficientPermissionsException;
import com.cyanogenmod.explorer.model.FileSystemObject;
import com.cyanogenmod.explorer.model.Symlink;
import com.cyanogenmod.explorer.util.CommandHelper;

/**
 * A class for testing the {@link LinkCommandTest} command.
 *
 * @see DeleteFileCommand
 */
public class LinkCommandTest extends AbstractConsoleTest {

    private static final String PATH_SOURCE_OK = "/data/source.txt"; //$NON-NLS-1$
    private static final String PATH_LINK_OK = "/data/source-link"; //$NON-NLS-1$
    private static final String PATH_SOURCE_ERROR = "/sdcard/source.txt"; //$NON-NLS-1$
    private static final String PATH_LINK_ERROR = "/sdcard/source-link"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRootConsoleNeeded() {
        return true;
    }

    /**
     * Method that performs a test to delete a file.
     *
     * @throws Exception If test failed
     */
    public void testCreateSymlinkOk() throws Exception {
        try {
            CommandHelper.createFile(getContext(), PATH_SOURCE_OK, getConsole());
            boolean ret = CommandHelper.createLink(
                    getContext(), PATH_SOURCE_OK, PATH_LINK_OK, getConsole());
            FileSystemObject fso =
                    CommandHelper.getFileInfo(getContext(), PATH_LINK_OK, false, getConsole());
            assertTrue("response==false", ret); //$NON-NLS-1$
            assertTrue("fso not is Symlink", fso instanceof Symlink); //$NON-NLS-1$
        } finally {
            try {
                CommandHelper.deleteFile(getContext(), PATH_SOURCE_OK, getConsole());
            } catch (Exception e) {/**NON BLOCK**/}
            try {
                CommandHelper.deleteFile(getContext(), PATH_LINK_OK, getConsole());
            } catch (Exception e) {/**NON BLOCK**/}
        }
    }

    /**
     * Method that performs a test to delete an invalid file.
     *
     * @throws Exception If test failed
     */
    public void testCreateSymlinkFail() throws Exception {
        try {
            CommandHelper.createFile(getContext(), PATH_SOURCE_ERROR, getConsole());
            boolean ret = CommandHelper.createLink(
                    getContext(), PATH_SOURCE_ERROR, PATH_LINK_ERROR, getConsole());
            assertTrue("response==false", ret); //$NON-NLS-1$
            try {
                FileSystemObject fso =
                        CommandHelper.getFileInfo(getContext(), PATH_LINK_ERROR, getConsole());
                assertTrue("fso != null", fso == null); //$NON-NLS-1$
            } catch (Exception e) {
                //OK. getFileInfo throws an exception because the symlink couldn't be created
            }
        } catch (InsufficientPermissionsException eex) {
            // This the expected behaviour because the symlink couldn't be created
        } finally {
            try {
                CommandHelper.deleteFile(getContext(), PATH_SOURCE_ERROR, getConsole());
            } catch (Exception e) {/**NON BLOCK**/}
            try {
                CommandHelper.deleteFile(getContext(), PATH_LINK_ERROR, getConsole());
            } catch (Exception e) {/**NON BLOCK**/}
        }
    }

}