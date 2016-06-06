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

import Maude.MaudeSpec;
import Maude.Operation;
import Maude.SModule;
import Maude.Type;

public class TestMaudeM2TOperators {
	
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
	 * Tests (for each case study), for each system module, it tests if an operator with that name exists
	 */
	@Test
	public void testOperatorNames() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.filter(m -> !Util.skippedModules().contains(m.getName()))
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(Operation oper : smod.getEls().stream()
						.filter(Operation.class::isInstance)
						.map(Operation.class::cast)
						.collect(Collectors.toList())) {
					/* there is an operator...... */
					assertTrue(result.indexOf("op " + oper.getName()) > -1);
					/* the sort is inside the module */
					assertTrue(result.indexOf("op " + oper.getName()) >
							result.indexOf("mod " + oper.getModule().getName() + "is"));
				}
			}
		}
	}
	
	/**
	 * Tests (for each case study), for each system module, it tests if the operator has its arity elements
	 */
	@Test
	public void testOperatorArity() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.filter(m -> !Util.skippedModules().contains(m.getName()))
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(Operation oper : smod.getEls().stream()
						.filter(Operation.class::isInstance)
						.map(Operation.class::cast)
						.collect(Collectors.toList())) {
					for(Type type : oper.getArity()) {
						assertTrue(result.indexOf(type.getName(), result.indexOf("op " + oper.getName())) > - 1);
					}
				}
			}
		}
	}
	
	/**
	 * Tests (for each case study), for each system module, it tests if the operator has its coarity
	 */
	@Test
	public void testOperatorCoarity() {
		for(MaudeSpec spec : models) {
			String result = transformation.generateCode(spec).toString();
			Set<SModule> smods = spec.getEls().stream()
					.filter(SModule.class::isInstance)
					.filter(m -> !Util.skippedModules().contains(m.getName()))
					.map(SModule.class::cast)
					.collect(Collectors.toSet());
			for(SModule smod : smods) {
				for(Operation oper : smod.getEls().stream()
						.filter(Operation.class::isInstance)
						.map(Operation.class::cast)
						.collect(Collectors.toList())) {
						assertTrue(result.indexOf(oper.getCoarity().getName(), result.indexOf("->")) > - 1);
				}
			}
		}
	}
}
