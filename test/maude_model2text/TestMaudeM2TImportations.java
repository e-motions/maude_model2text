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

import maude_model2text.Main.Util;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.BeforeClass;
import org.junit.Test;

import Maude.ImportationMode;
import Maude.MaudeSpec;
import Maude.ModImportation;
import Maude.ModuleIdModExp;
import Maude.SModule;

public class TestMaudeM2TImportations {
	
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
	 * Tests (for each case study), for each system module, it tests the *protecting* importations without
	 * renamings
	 */
	@Test
	public void testProtectingSModules() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(ModImportation pr : smod.getEls().stream()
						.filter(ModImportation.class::isInstance)
						.map(ModImportation.class::cast)
						.filter(imp -> imp.getMode() == ImportationMode.PROTECTING)
						.collect(Collectors.toList())) {
					ModuleIdModExp modImp = (ModuleIdModExp) pr.getImports();
					/* the protecting sentence exists in the file */
					assertTrue(result.indexOf("pr " + modImp.getModule().getName()) > -1 ||
							result.indexOf("protecting " + modImp.getModule().getName()) > -1);
					/* the protecting sentence exists inside its system module */
					if(result.indexOf("pr " + modImp.getModule().getName()) > -1) {
						assertTrue(result.indexOf("pr " + modImp.getModule().getName()) >
								result.indexOf("mod " + smod.getName() + " is"));
					} else {
						assertTrue(result.indexOf("protecting " + modImp.getModule().getName()) >
						result.indexOf("mod " + smod.getName() + " is"));
					}
				}
			}
		}
	}
	
	/**
	 * Tests (for each case study), for each system module, it tests the *including* importations without
	 * renamings
	 */
	@Test
	public void testIncludingSModules() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(ModImportation pr : smod.getEls().stream()
						.filter(ModImportation.class::isInstance)
						.map(ModImportation.class::cast)
						.filter(imp -> imp.getMode() == ImportationMode.INCLUDING)
						.collect(Collectors.toList())) {
					ModuleIdModExp modImp = (ModuleIdModExp) pr.getImports();
					/* the protecting sentence exists in the file */
					assertTrue(result.indexOf("inc " + modImp.getModule().getName()) > -1 ||
							result.indexOf("including " + modImp.getModule().getName()) > -1);
					/* the protecting sentence exists inside its system module */
					if(result.indexOf("inc " + modImp.getModule().getName()) > -1) {
						assertTrue(result.indexOf("inc " + modImp.getModule().getName()) >
								result.indexOf("mod " + smod.getName() + " is"));
					} else {
						assertTrue(result.indexOf("including " + modImp.getModule().getName()) >
						result.indexOf("mod " + smod.getName() + " is"));
					}
				}
			}
		}
	}
	
	/**
	 * Tests (for each case study), for each system module, it tests the *including* importations without
	 * renamings
	 */
	@Test
	public void testExtendingSModules() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(ModImportation pr : smod.getEls().stream()
						.filter(ModImportation.class::isInstance)
						.map(ModImportation.class::cast)
						.filter(imp -> imp.getMode() == ImportationMode.EXTENDING)
						.collect(Collectors.toList())) {
					ModuleIdModExp modImp = (ModuleIdModExp) pr.getImports();
					/* the protecting sentence exists in the file */
					assertTrue(result.indexOf("ext " + modImp.getModule().getName()) > -1 ||
							result.indexOf("extending " + modImp.getModule().getName()) > -1);
					/* the protecting sentence exists inside its system module */
					if(result.indexOf("ext " + modImp.getModule().getName()) > -1) {
						assertTrue(result.indexOf("ext " + modImp.getModule().getName()) >
								result.indexOf("mod " + smod.getName() + " is"));
					} else {
						assertTrue(result.indexOf("extending " + modImp.getModule().getName()) >
						result.indexOf("mod " + smod.getName() + " is"));
					}
				}
			}
		}
	}

}
