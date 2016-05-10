/**
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
