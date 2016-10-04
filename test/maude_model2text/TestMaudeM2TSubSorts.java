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

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import Maude.MaudeSpec;
import Maude.SModule;
import Maude.SubsortRel;

public class TestMaudeM2TSubSorts {
	
	private static MaudeM2T transformation;
	private static Set<MaudeSpec> models;
	
	@BeforeClass
	public static void loadModels(){
		transformation = new MaudeM2T();
		models = new HashSet<>();
		for(String caseStudy : Util.CASE_STUDIES) {
			Resource res = transformation.load(Util.getPath(caseStudy));
			MaudeSpec spec = (MaudeSpec) res.getContents().stream()
					.filter(o -> o instanceof MaudeSpec)
					.findAny()
					.orElse(null);
			if(spec != null) {
				models.add(spec);
			}
		}
	}
	
	/**
	 * Tests (for each case study), for each system module, it tests the subsort declarations
	 */
	@Test
	public void testSubsorts() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.filter(m -> !Util.skippedModules().contains(m.getName()))
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(SubsortRel ssort : smod.getEls().stream()
						.filter(SubsortRel.class::isInstance)
						.map(SubsortRel.class::cast)
						.collect(Collectors.toList())) {
					/* there is a subsort... */
					assertTrue(result.indexOf("subsort " + ssort.getSubsorts().get(0).getName() + " < " 
							+ ssort.getSupersorts().get(0).getName()) > -1);
					/* the sort is inside the module */
					assertTrue(result.indexOf("subsort " + ssort.getSubsorts().get(0).getName() + " < " 
							+ ssort.getSupersorts().get(0).getName()) >
							result.indexOf("mod " + ssort.getModule().getName() + "is"));
				}
			}
		}
	}
}
