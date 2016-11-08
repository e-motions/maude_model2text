/*
 * Copyright (c) 2016 e-Motions
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (e-Motions), to deal in e-Motions without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package maude_model2text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Util {
	public final static String PATH = "resources/";
	public final static String[] CASE_STUDIES = {
		"prTrajectory_behavior.xmi",
		"prCRA_behavior.xmi",
		"palladio_behavior.xmi",
		"prTrajectory_metamodel.xmi",
		"prCRA_metamodel.xmi",
		"palladio_metamodel.xmi",
		"pls.xmi"};
	
	public final static String[] SKIPPED_MODULES = {
		"@ECORE@"
	};
	
	public static String getPath(String caseStudy) {
		return PATH + "/" + caseStudy;
	}
	
	public static Set<String> skippedModules() {
		return new HashSet<String>(Arrays.asList(SKIPPED_MODULES));
	}
}
