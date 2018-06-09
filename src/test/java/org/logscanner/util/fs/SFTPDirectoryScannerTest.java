/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.logscanner.util.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.selectors.TokenizedPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.logscanner.util.fs.LocalDirectoryScanner;

/**
 */
public class SFTPDirectoryScannerTest {

    @Before
    public void setUp() {
    }
    
    private SFTPDirectoryScanner createDirectoryScanner()
    {
    	SFTPDirectoryScanner result = new SFTPDirectoryScanner();
    	result.setHost("127.0.0.1");
    	result.setUsername("victor");
    	result.setPassword("1qaz2wsx");
    	return result;
    }
    
	private String getBaseDir() 
	{
		return "/Users/victor/victor/Projects/logscanner/logscanner/src/test/resources/sftpdirectoryscanner/base";
	}    
    
	private String getExtendedDir() 
	{
		return "/Users/victor/victor/Projects/logscanner/logscanner/src/test/resources/sftpdirectoryscanner/extended";
	}    

	private String getSymlinkDir() 
	{
		return "/Users/victor/victor/Projects/logscanner/logscanner/src/test/resources/sftpdirectoryscanner/symlink";
	}    

	@Test
    public void test1() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha"});
        ds.scan();
        compareFiles(ds, new String[] {}, new String[] {"alpha"});
    }

    @Test
    public void test2() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                       "alpha/beta/gamma/gamma.xml"},
                     new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

    @Test
    public void test3() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                       "alpha/beta/gamma/gamma.xml"},
                     new String[] {"", "alpha", "alpha/beta",
                                   "alpha/beta/gamma"});
    }

    @Test
    public void testFullPathMatchesCaseSensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/GAMMA.XML"});
        ds.scan();
        compareFiles(ds, new String[] {}, new String[] {});
    }

    @Test
    public void testFullPathMatchesCaseInsensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setCaseSensitive(false);
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/GAMMA.XML"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                new String[] {});
    }

    @Test
    public void test2ButCaseInsensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"ALPHA/"});
        ds.setCaseSensitive(false);
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml", "alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

    @Test
    @Ignore
    public void testAllowSymlinks() {

        LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getSymlinkDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha/beta/gamma"});
    }

    @Test
    public void testProhibitSymlinks() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getSymlinkDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/"});
        ds.setFollowSymlinks(false);
        ds.scan();
        compareFiles(ds, new String[] {}, new String[] {});
    }

    // father and child pattern test
    @Test
    public void testOrderOfIncludePatternsIrrelevant() {
        String[] expectedFiles = {"alpha/beta/beta.xml",
                                   "alpha/beta/gamma/gamma.xml"};
        String[] expectedDirectories = {"alpha/beta", "alpha/beta/gamma" };
        LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/be?a/**", "alpha/beta/gamma/"});
        ds.scan();
        compareFiles(ds, expectedFiles, expectedDirectories);
        // redo the test, but the 2 include patterns are inverted
        ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/", "alpha/be?a/**"});
        ds.scan();
        compareFiles(ds, expectedFiles, expectedDirectories);
    }

    @Test
    public void testPatternsDifferInCaseScanningSensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/", "ALPHA/"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml", "alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

    @Test
    public void testPatternsDifferInCaseScanningInsensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/", "ALPHA/"});
        ds.setCaseSensitive(false);
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml", "alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

    @Test
    public void testFullpathDiffersInCaseScanningSensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/gamma.xml", "alpha/beta/gamma/GAMMA.XML"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"}, new String[] {});
    }

    @Test
    public void testFullpathDiffersInCaseScanningInsensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/beta/gamma/gamma.xml", "alpha/beta/gamma/GAMMA.XML"});
        ds.setCaseSensitive(false);
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"}, new String[] {});
    }

    @Test
    public void testParentDiffersInCaseScanningSensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/", "ALPHA/beta/"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml", "alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

    @Test
    public void testParentDiffersInCaseScanningInsensitive() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/", "ALPHA/beta/"});
        ds.setCaseSensitive(false);
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml", "alpha/beta/gamma/gamma.xml"},
                new String[] {"alpha", "alpha/beta", "alpha/beta/gamma"});
    }

//    /**
//     * Test case for setFollowLinks() and associated functionality.
//     * Only supports test on Linux at the moment because Java has
//     * no real notion of symlinks built in, so an os-specfic call
//     * to Runtime.exec() must be made to create a link to test against.
//     * @throws InterruptedException if something goes wrong
//     */
//    @Test
//    public void testSetFollowLinks() throws IOException, InterruptedException {
//        if (supportsSymlinks) {
//            File linkFile = new File(System.getProperty("root"), "src/main/org/apache/tools/ThisIsALink");
//            System.err.println("link exists pre-test? " + linkFile.exists());
//
//            try {
//                // add conditions and more commands as soon as the need arises
//                String[] command = new String[] {"ln", "-s", "ant", linkFile.getAbsolutePath()};
//                Process process = Runtime.getRuntime().exec(command);
//                assertEquals("0 return code expected for external process", 0, process.waitFor());
//
//                File dir = new File(System.getProperty("root"), "src/main/org/apache/tools");
//
//                SFTPDirectoryScanner ds = createSFTPDirectoryScanner();
//
//                // followLinks should be true by default, but if this ever
//                // changes we will need this line.
//                ds.setFollowSymlinks(true);
//
//                ds.setBasedir(dir);
//                ds.setExcludes(new String[] {"ant/**"});
//                ds.scan();
//
//                boolean haveZipPackage = false;
//                boolean haveTaskdefsPackage = false;
//
//                String[] includeds = ds.getIncludedDirectories();
//                for (String included : includeds) {
//                    if (included.equals("zip")) {
//                        haveZipPackage = true;
//                    } else if (included.equals("ThisIsALink" + File.separator + "taskdefs")) {
//                        haveTaskdefsPackage = true;
//                    }
//                }
//
//                // if we followed the symlink we just made we should
//                // bypass the excludes.
//
//                assertTrue("(1) zip package included", haveZipPackage);
//                assertTrue("(1) taskdefs package included", haveTaskdefsPackage);
//
//                ds = createSFTPDirectoryScanner();
//                ds.setFollowSymlinks(false);
//
//                ds.setBasedir(dir);
//                ds.setExcludes(new String[] {"ant/**"});
//                ds.scan();
//
//                haveZipPackage = false;
//                haveTaskdefsPackage = false;
//                includeds = ds.getIncludedDirectories();
//                for (String included : includeds) {
//                    if (included.equals("zip")) {
//                        haveZipPackage = true;
//                    } else if (included.equals("ThisIsALink" + File.separator + "taskdefs")) {
//                        haveTaskdefsPackage = true;
//                    }
//                }
//                assertTrue("(2) zip package included", haveZipPackage);
//                assertFalse("(2) taskdefs package not included", haveTaskdefsPackage);
//
//            } finally {
//                if (!linkFile.delete()) {
//                    //TODO log this?
//                    //throw new RuntimeException("Failed to delete " + linkFile);
//                }
//
//            }
//        }
//    }

    @Test
    public void testExcludeOneFile() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"**/*.xml"});
        ds.setExcludes(new String[] {"alpha/beta/b*xml"});
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/gamma/gamma.xml"},
                new String[] {});
    }

    @Test
    public void testExcludeHasPrecedence() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/**"});
        ds.setExcludes(new String[] {"alpha/**"});
        ds.scan();
        compareFiles(ds, new String[] {},
                new String[] {});
    }

    @Test
    public void testAlternateIncludeExclude() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setIncludes(new String[] {"alpha/**", "alpha/beta/gamma/**"});
        ds.setExcludes(new String[] {"alpha/beta/**"});
        ds.scan();
        compareFiles(ds, new String[] {},
                new String[] {"alpha"});
    }

    @Test
    public void testAlternateExcludeInclude() {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setExcludes(new String[] {"alpha/**", "alpha/beta/gamma/**"});
        ds.setIncludes(new String[] {"alpha/beta/**"});
        ds.scan();
        compareFiles(ds, new String[] {},
                new String[] {});
    }

    /**
     * Test inspired by Bug#1415.
     */
    @Test
    public void testChildrenOfExcludedDirectory() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getExtendedDir());
        ds.setExcludes(new String[] {"alpha/**"});
        ds.setFollowSymlinks(false);
        ds.scan();
        compareFiles(ds, new String[] {"delta/delta.xml"},
                    new String[] {"", "delta"});

        ds = createDirectoryScanner();
        ds.setBasedir(getExtendedDir());
        ds.setExcludes(new String[] {"alpha"});
        ds.setFollowSymlinks(false);
        ds.scan();
        compareFiles(ds, new String[] {"alpha/beta/beta.xml",
                                       "alpha/beta/gamma/gamma.xml",
                                        "delta/delta.xml"},
                     new String[] {"", "alpha/beta", "alpha/beta/gamma", "delta"});

    }

//    @Test
//    public void testIsExcludedDirectoryScanned() {
//        String shareclassloader = buildRule.getProject().getProperty("tests.and.ant.share.classloader");
//        // when the test is started by the build.xml of ant
//        // if the property tests.and.ant.share.classloader is not set in the build.xml
//        // a sysproperty with name tests.and.ant.share.classloader and value
//        // ${tests.and.ant.share.classloader} will be set
//        // we are trying to catch this here.
//        assumeFalse("cannot execute testIsExcludedDirectoryScanned when tests are forked, "
//                + "package private method called", shareclassloader == null
//                        || shareclassloader.indexOf("${") == 0);
//        SFTPDirectoryScanner ds = createSFTPDirectoryScanner();
//        ds.setBasedir(getExtendedDir());
//
//        ds.setExcludes(new String[] {"**/gamma/**"});
//        ds.setFollowSymlinks(false);
//        ds.scan();
//        Set<String> set = ds.getScannedDirs();
//        assertFalse("empty set", set.isEmpty());
//        String s = "alpha/beta/gamma/".replace('/', File.separatorChar);
//        assertFalse("scanned " + s, set.contains(s));
//    }

    @Test
    public void testAbsolute1() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        String tmpdir = getExtendedDir();
        ds.setIncludes(new String[] {tmpdir + "/**/*"});
        ds.scan();
        compareFiles(ds,
                new String[] {tmpdir + "/alpha/beta/beta.xml",
                        tmpdir + "/alpha/beta/gamma/gamma.xml",
                        tmpdir + "/delta/delta.xml"},
                new String[] {tmpdir + "/alpha",
                        tmpdir + "/alpha/beta",
                        tmpdir + "/alpha/beta/gamma",
                        tmpdir + "/delta"});
    }

//    @Test
//    public void testAbsolute2() {
//    	SFTPDirectoryScanner ds = createSFTPDirectoryScanner();
//        ds.setIncludes(new String[] {"alpha/**", "alpha/beta/gamma/**"});
//        ds.scan();
//        String[] mt = new String[0];
//        compareFiles(ds, mt, mt);
//    }

    @Test
    public void testAbsolute3() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        String tmpdir = getExtendedDir();
        ds.setIncludes(new String[] {tmpdir + "/**/*"});
        ds.setExcludes(new String[] {"**/alpha", "**/delta/*"});
        ds.scan();
        compareFiles(ds,
                new String[] {tmpdir + "/alpha/beta/beta.xml",
                        tmpdir + "/alpha/beta/gamma/gamma.xml"},
                new String[] {tmpdir + "/alpha/beta",
                        tmpdir + "/alpha/beta/gamma",
                        tmpdir + "/delta"});
    }

    @Test
    public void testAbsolute4() {
        LocalDirectoryScanner ds = createDirectoryScanner();
        String tmpdir = getExtendedDir();
        ds.setIncludes(new String[] {tmpdir + "/alpha/beta/**/*", tmpdir + "/delta/*"});
        ds.setExcludes(new String[] {"**/beta.xml"});
        ds.scan();
        compareFiles(ds,
                new String[] {tmpdir + "/alpha/beta/gamma/gamma.xml",
                        tmpdir + "/delta/delta.xml"},
                new String[] {tmpdir + "/alpha/beta/gamma"});
    }

//    @Test
//    public void testAbsolute5() {
//        //testing drive letter search from root:
//        assumeTrue("Can't use drive letters on non DOS or Netware systems",
//                Os.isFamily("dos") || Os.isFamily("netware"));
//        SFTPDirectoryScanner ds = createSFTPDirectoryScanner();
//        String pattern = new File(File.separator).getAbsolutePath().toUpperCase() + "*";
//        ds.setIncludes(new String[] {pattern});
//        ds.scan();
//        //if this is our context we assume there must be something here:
//        assertTrue("should have at least one resident file",
//            ds.getIncludedFilesCount() + ds.getIncludedDirsCount() > 0);
//    }

    private void compareFiles(LocalDirectoryScanner ds, String[] expectedFiles,
                              String[] expectedDirectories) {
        String[] includedFiles = ds.getIncludedFiles();
        String[] includedDirectories = ds.getIncludedDirectories();
        assertEquals("file present: ", expectedFiles.length,
                     includedFiles.length);
        assertEquals("directories present: ", expectedDirectories.length,
                     includedDirectories.length);

        TreeSet<String> files = new TreeSet<String>();
        for (String includedFile : includedFiles) {
            files.add(includedFile.replace(File.separatorChar, '/'));
        }
        TreeSet<String> directories = new TreeSet<String>();
        for (String includedDirectory : includedDirectories) {
            directories.add(includedDirectory.replace(File.separatorChar, '/'));
        }

        String currentfile;
        Iterator<String> i = files.iterator();
        int counter = 0;
        while (i.hasNext()) {
            currentfile = i.next();
            assertEquals(expectedFiles[counter], currentfile);
            counter++;
        }
        String currentdirectory;
        Iterator<String> dirit = directories.iterator();
        counter = 0;
        while (dirit.hasNext()) {
            currentdirectory = dirit.next();
            assertEquals(expectedDirectories[counter], currentdirectory);
            counter++;
        }
    }

    @Test
    public void testRecursiveExcludes() throws Exception {
    	LocalDirectoryScanner ds = createDirectoryScanner();
        ds.setBasedir(getBaseDir());
        ds.setExcludes(new String[] {"**/beta/**"});
        ds.scan();
        List<String> dirs = Arrays.asList(ds.getExcludedDirectories());
        assertEquals(2, dirs.size());
        assertTrue("beta is excluded", dirs.contains("alpha/beta"
                .replace('/', File.separatorChar)));
        assertTrue("gamma is excluded", dirs.contains("alpha/beta/gamma"
                .replace('/', File.separatorChar)));
        List<String> files = Arrays.asList(ds.getExcludedFiles());
        assertEquals(2, files.size());
        assertTrue("beta.xml is excluded", files.contains("alpha/beta/beta.xml"
                .replace('/', File.separatorChar)));
        assertTrue("gamma.xml is excluded", files.contains("alpha/beta/gamma/gamma.xml"
                .replace('/', File.separatorChar)));
    }

//    @Test
//    public void testContentsExcluded() {
//    	SFTPDirectoryScanner ds = createSFTPDirectoryScanner();
//        ds.setBasedir(new File("."));
//        ds.setIncludes(new String[] {"**"});
//        ds.addDefaultExcludes();
//        ds.ensureNonPatternSetsReady();
//        File f = new File(".svn");
//        TokenizedPath p = new TokenizedPath(f.getAbsolutePath());
//        assertTrue(ds.contentsExcluded(p));
//    }
}
