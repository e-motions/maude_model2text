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

import Maude.ImportationMode
import Maude.MaudePackage
import Maude.MaudeSpec
import Maude.ModImportation
import Maude.Module
import Maude.ModuleIdModExp
import Maude.SModule
import Maude.Sort
import Maude.SubsortRel
import java.io.PrintWriter
import maude_model2text.Main.Util
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

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
      ---- Importations
      «generateImportations(smod)»
      
      ---- Sort declarations
      «generateSortDeclarations(smod)»
      
      ---- Subsort declarations
      «generateSubsortDeclarations(smod)»
    endm
    «ENDFOR»
    '''
    
    /**
     * Given a Maude module, it generates all importations.
     * @params
     *  mod    the Module with none or more ModImportation objects
     */
    def generateImportations(Module mod) '''
    «FOR imp:mod.els.filter(typeof(ModImportation))»
    «IF imp.mode == ImportationMode.PROTECTING && imp.imports instanceof ModuleIdModExp»
    pr «(imp.imports as ModuleIdModExp).module.name» .
    «ELSEIF imp.mode == ImportationMode.INCLUDING && imp.imports instanceof ModuleIdModExp»
    inc «(imp.imports as ModuleIdModExp).module.name» .
    «ELSEIF imp.mode == ImportationMode.EXTENDING && imp.imports instanceof ModuleIdModExp»
    ext «(imp.imports as ModuleIdModExp).module.name» .
    «ENDIF»
    «ENDFOR»
    '''
    
    /**
     * Given a Maude module, it generates all sort declarations.
     * @params
     *  mod    the Module with none or more sort objects
     */
    def generateSortDeclarations(Module mod) '''
    «FOR sort:mod.els.filter(typeof(Sort))»
    sort «sort.name» .
    «ENDFOR»
    '''
    
    /**
     * Given a Maude module, it generates all subsort declarations.
     * @params
     *  mod    the Module with none or more sort objects
     */
    def generateSubsortDeclarations(Module mod) '''
    «FOR ssort:mod.els.filter(typeof(SubsortRel))»
    subsort «ssort.subsorts.get(0).name» < «ssort.supersorts.get(0).name» .
    «ENDFOR»
    '''

}
