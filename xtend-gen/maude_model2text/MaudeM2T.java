package maude_model2text;

import Maude.MaudeSpec;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

@SuppressWarnings("all")
public class MaudeM2T {
  /**
   * Generates the model's code given as parameter 'model' in the file 'output'
   * @precondition: model and output exist. 'model' points out to a Maude model with only
   * one 'MaudeSpec' element.
   */
  public void generate(final String model, final String output) {
    this.doEMFSetup();
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    URI _createURI = URI.createURI(model);
    final Resource resource = resourceSet.getResource(_createURI, true);
    EList<EObject> _contents = resource.getContents();
    for (final EObject content : _contents) {
    }
  }
  
  /**
   * Registers the "xmi" extension in the EMF.Ecore factory.
   */
  public Object doEMFSetup() {
    Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    return _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
  }
  
  public Object generateCode(final MaudeSpec mspec) {
    return null;
  }
}
