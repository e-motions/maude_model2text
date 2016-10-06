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

package maude_model2text

import Maude.BooleanCond
import Maude.Condition
import Maude.Constant
import Maude.Equation
import Maude.ImportationMode
import Maude.MatchingCond
import Maude.MaudePackage
import Maude.MaudeSpec
import Maude.ModImportation
import Maude.Module
import Maude.ModuleIdModExp
import Maude.Operation
import Maude.RecTerm
import Maude.SModule
import Maude.Sort
import Maude.SubsortRel
import Maude.Term
import Maude.Type
import Maude.Variable
import java.io.PrintWriter
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import Maude.Rule

class MaudeM2T {
    
    new(){
        doEMFSetup
    }

    /**
     * Generates the model's code given as parameter 'model' in the file 'output'
     * @precondition: the model exists. 'model' points out to a Maude model with only 
     * one 'MaudeSpec' element.
     */
    def generate(String model, String output) {
        val result = generate(load(model))
        val writer = new PrintWriter(output, "UTF-8")
        writer.print(result)
        writer.close()
    }
    
    /**
     * Given a path to a model (in XMI format), it loads it and returns a Resource
     */
    def load(String model) {
        val resourceSet = new ResourceSetImpl
        resourceSet.getPackageRegistry().put(
            MaudePackage.eINSTANCE.getNsURI(),
            MaudePackage.eINSTANCE
        )
        return resourceSet.getResource(URI.createURI(model), true)
    }
    
    /**
     * Given a path to a model, it returns a String with the code corresponding to such model
     */
    def generate(String model) {
        val resourceSet = new ResourceSetImpl
        val resource = resourceSet.getResource(URI.createURI(model), true)
        return generate(resource)
    }
    
    /**
     * Given a model, it returns a String with the code corresponding to such model
     */
    def generate(Resource model) {
        var result = ""
        for(content : model.contents) {
            if(content instanceof MaudeSpec) {
                result += generateCode(content as MaudeSpec)
            }
        }
        return result
    }
    
    /**
     * Registers the "xmi" extension in the EMF.Ecore factory.
     */
    def doEMFSetup() {
        Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl);
    }
    
    /**
     * Given a Maude specification, it generates the Maude code
     */
    def generateCode(MaudeSpec mspec) '''
    «FOR smod:mspec.els.filter(typeof(SModule)).filter[Module m | !Util.skippedModules().contains(m.name)]»
    mod «smod.name» is
      «generateImportations(smod.els.filter(typeof(ModImportation)))»
      «generateSortDeclarations(smod.els.filter(typeof(Sort)))»
      «generateSubsortDeclarations(smod.els.filter(typeof(SubsortRel)))»
      «generateOperations(smod.els.filter(typeof(Operation)))»
      «generateEquations(smod.els.filter(typeof(Equation)))»
      «generateRules(smod.els.filter(typeof(Rule)))»
    endm
    «ENDFOR»
    '''
    
    /**
     * Given a Maude module, it generates all importations.
     * @params
     *  mod    the Module with none or more ModImportation objects
     */
    def generateImportations(Iterable<ModImportation> importations) '''
    «IF importations.size > 0»
     
    ---- <begin> Importations«ENDIF»
    «FOR imp:importations»
    «IF imp.mode == ImportationMode.PROTECTING && imp.imports instanceof ModuleIdModExp»
    pr «(imp.imports as ModuleIdModExp).module.name» .
    «ELSEIF imp.mode == ImportationMode.INCLUDING && imp.imports instanceof ModuleIdModExp»
    inc «(imp.imports as ModuleIdModExp).module.name» .
    «ELSEIF imp.mode == ImportationMode.EXTENDING && imp.imports instanceof ModuleIdModExp»
    ext «(imp.imports as ModuleIdModExp).module.name» .
    «ENDIF»
    «ENDFOR»
    «IF importations.size > 0»---- <end> Importations«ENDIF»
    '''
    
    /**
     * Given a Maude module, it generates all sort declarations.
     * @params
     *  mod    the Module with none or more sort objects
     */
    def generateSortDeclarations(Iterable<Sort> sorts) '''
    «IF !sorts.empty»
     
    ---- <begin> Sort declarations«ENDIF»
    «FOR sort:sorts»
    sort «sort.name» .
    «ENDFOR»
    «IF !sorts.empty»---- <end> Sort declarations«ENDIF»
    '''
    
    /**
     * Given a Maude module, it generates all subsort declarations.
     * @params
     *  mod    the Module with none or more sort objects
     */
    def generateSubsortDeclarations(Iterable<SubsortRel> ssorts) '''
    «IF !ssorts.empty»
     
    ---- <begin> Subsort declarations«ENDIF»
    «FOR ssort:ssorts»
    subsort «ssort.subsorts.get(0).name» < «ssort.supersorts.get(0).name» .
    «ENDFOR»
    «IF !ssorts.empty»---- <end> Subsort declarations«ENDIF»
    '''
    
    def generateOperations(Iterable<Operation> operations) '''
    «IF !operations.empty»
     
    ---- <begin> Operation declarations«ENDIF»
    «FOR op:operations»
    op «op.name» : «printArity(op.arity)»-> «printCoarity(op.coarity)» «IF !op.atts.empty»[«printAtts(op.atts)»] «ENDIF».
    «ENDFOR»
    «IF !operations.empty»---- <end> Operation declarations«ENDIF»
    '''
    
    def printAtts(EList<String> list) '''
    «FOR a : list»
    «a»«IF !a.equals(list.get(list.size-1))» «ENDIF»«ENDFOR»'''
    
    
    def printArity(EList<Type> list) '''«FOR t : list SEPARATOR " " AFTER " "»«t.name»«ENDFOR»'''
    
    def printCoarity(Type type) '''«type.name»'''
    
    def generateEquations(Iterable<Equation> equations) '''
    «IF !equations.empty»
     
    ---- <begin> Equations«ENDIF»
    «FOR eq : equations SEPARATOR "\n"»
    «IF eq.conds.empty»«printNoConditionalEq(eq)»«ELSE»«printConditionalEq(eq)»«ENDIF»«ENDFOR»
    «IF !equations.empty»---- <end> Equations«ENDIF»
    '''
    
    def printConditionalEq(Equation equation) '''
    ceq «IF equation.label != null && !equation.label.equals("")»[«equation.label»] : «ENDIF»«printTerm(equation.lhs)»
      = «printTerm(equation.rhs)»
      if «printConditions(equation.conds)» «IF !equation.atts.empty»[«printAtts(equation.atts)»] «ENDIF».
    '''
    
    def printConditions(EList<Condition> conditions) '''«FOR cond : conditions SEPARATOR "\n/\\ "»«printCond(cond)»«ENDFOR»'''
    
    def printCond(Condition condition) {
        if(condition instanceof MatchingCond) {
            printMatchingCond(condition as MatchingCond)
        } else if(condition instanceof BooleanCond) {
            printBooleanCond(condition as BooleanCond)
        }
    }
    
    def printMatchingCond(MatchingCond cond) '''«printTerm(cond.lhs)» := «printTerm(cond.rhs)»'''
        
    def printBooleanCond(BooleanCond cond) '''«printTerm(cond.lhs)»'''
    
    def printNoConditionalEq(Equation equation) '''
    eq «IF equation.label != null && !equation.label.equals("")»[«equation.label»] : «ENDIF»«printTerm(equation.lhs)»
      = «printTerm(equation.rhs)» «IF !equation.atts.empty»[«printAtts(equation.atts)»] «ENDIF».
    '''
    
    def printTerm(Term term) {
        if(term instanceof Variable) {
            printVariable(term)
        } else if(term instanceof Constant) {
            printConstant(term)
        } else if (term instanceof RecTerm){
            printRecTerm(term)
        }
    }
    
    def printRecTerm(RecTerm rt) '''
    «IF rt.op.equals(MaudeOperators.MODEL)
     »«"\n  "»«
    ELSEIF rt.op.equals(MaudeOperators.SET)»«"\n    "
    »«ELSEIF rt.op.equals(MaudeOperators.OBJECT) ||  rt.op.equals(MaudeOperators.COMPLETE)»«"\n    "
    »«ELSEIF rt.op.equals(MaudeOperators.SET_SF) ||  rt.op.equals(MaudeOperators.SF)»«"\n      "
    »«ENDIF»«rt.op»«FOR t : rt.args BEFORE "(" SEPARATOR "," AFTER ")"»«printTerm(t as Term)»«ENDFOR»'''

    def printConstant(Constant constant) '''«constant.op»'''
    
    def printVariable(Variable variable) '''«IF variable.type.name.equals("Set{@StructuralFeatureInstance}")»«"\n      "
    »«ELSEIF variable.type.name.equals("Set{@Object}")»«"\n    "»«ENDIF»«variable.name»:«variable.type.name»'''
    
    def generateRules(Iterable<Rule> rules) '''
    «IF !rules.empty»
     
    ---- <begin> Rules«ENDIF»
    «FOR rl : rules SEPARATOR "\n"»
    «IF rl.conds.empty»«printNoConditionalRule(rl)»«ELSE»«printConditionalRule(rl)»«ENDIF»«ENDFOR»
    «IF !rules.empty»---- <end> Rules«ENDIF»
    '''
    
    def printNoConditionalRule(Rule rl) '''
    rl «IF rl.label != null && !rl.label.equals("")»[«rl.label»] : «ENDIF»«printTerm(rl.lhs)»
      => «printTerm(rl.rhs)» «IF !rl.atts.empty»[«printAtts(rl.atts)»] «ENDIF».
    '''
    
    def printConditionalRule(Rule rl) '''
    crl «IF rl.label != null && !rl.label.equals("")»[«rl.label»] : «ENDIF»«printTerm(rl.lhs)»
      => «printTerm(rl.rhs)»
      if «printConditions(rl.conds)» «IF !rl.atts.empty»[«printAtts(rl.atts)»] «ENDIF».
    '''
}
