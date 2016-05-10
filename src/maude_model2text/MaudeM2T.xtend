package maude_model2text

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource$Factory$Registry
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.ecore.resource.Resource

import Maude.*

class MaudeM2T {

    /**
     *
     * Generates the model's code given as parameter 'model' in the file 'output'
     * @precondition: model and output exist. 'model' points out to a Maude model with only 
     * one 'MaudeSpec' element.
     * 
     */
    def generate(String model, String output) {
        doEMFSetup
        val resourceSet = new ResourceSetImpl
        val resource = resourceSet.getResource(URI.createURI(model), true)
        for (content : resource.contents) {
//            generateCode(content)
        }
    }
    
    /**
     * Registers the "xmi" extension in the EMF.Ecore factory.
     */
    def doEMFSetup() {
        Resource$Factory.Registry.INSTANCE.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl);
    }
    
    def generateCode(MaudeSpec mspec) {
        
    }

}
