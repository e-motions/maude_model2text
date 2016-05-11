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

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import maude_model2text.Main.Util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import Maude.MaudeSpec;

public class TestMaudeM2TMaudeSpec {

	private static MaudeM2T transformation;
	
	@BeforeClass
	public static void loadModels(){
		transformation = new MaudeM2T();
	}
	
	/**
	 * There is only one MaudeSpec per model
	 */
	@Test
	public void testMaudeSpec() {
		for(String caseStudy : Util.CASE_STUDIES) {
			Resource res = transformation.load(Util.getPath(caseStudy));
			Set<MaudeSpec> specs = new HashSet<>();
			for(EObject obj : res.getContents()) {
				if(obj instanceof MaudeSpec) {
					specs.add((MaudeSpec) obj);
				}
			}
			assertEquals(specs.size(), 1);
		}
	}
}
