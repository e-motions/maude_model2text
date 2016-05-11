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

import Maude.ImportationMode;
import Maude.MaudePackage;
import Maude.MaudeSpec;
import Maude.MaudeTopEl;
import Maude.ModElement;
import Maude.ModExpression;
import Maude.ModImportation;
import Maude.Module;
import Maude.ModuleIdModExp;
import Maude.SModule;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.io.PrintWriter;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class MaudeM2T {
  public MaudeM2T() {
    this.doEMFSetup();
  }
  
  /**
   * Generates the model's code given as parameter 'model' in the file 'output'
   * @precondition: the model exists. 'model' points out to a Maude model with only
   * one 'MaudeSpec' element.
   */
  public void generate(final String model, final String output) {
    try {
      Resource _load = this.load(model);
      final String result = this.generate(_load);
      final PrintWriter writer = new PrintWriter(output, "UTF-8");
      writer.print(result);
      writer.close();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Given a path to a model (in XMI format), it loads it and returns a Resource
   */
  public Resource load(final String model) {
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    EPackage.Registry _packageRegistry = resourceSet.getPackageRegistry();
    String _nsURI = MaudePackage.eINSTANCE.getNsURI();
    _packageRegistry.put(_nsURI, 
      MaudePackage.eINSTANCE);
    URI _createURI = URI.createURI(model);
    return resourceSet.getResource(_createURI, true);
  }
  
  /**
   * Given a path to a model, it returns a String with the code corresponding to such model
   */
  public String generate(final String model) {
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    URI _createURI = URI.createURI(model);
    final Resource resource = resourceSet.getResource(_createURI, true);
    return this.generate(resource);
  }
  
  /**
   * Given a model, it returns a String with the code corresponding to such model
   */
  public String generate(final Resource model) {
    String result = "";
    EList<EObject> _contents = model.getContents();
    for (final EObject content : _contents) {
      if ((content instanceof MaudeSpec)) {
        String _result = result;
        CharSequence _generateCode = this.generateCode(((MaudeSpec) content));
        result = (_result + _generateCode);
      }
    }
    return result;
  }
  
  /**
   * Registers the "xmi" extension in the EMF.Ecore factory.
   */
  public Object doEMFSetup() {
    Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    return _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
  }
  
  /**
   * Given a Maude specification, it generates the Maude code
   */
  public CharSequence generateCode(final MaudeSpec mspec) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<MaudeTopEl> _els = mspec.getEls();
      Iterable<SModule> _filter = Iterables.<SModule>filter(_els, SModule.class);
      for(final SModule smod : _filter) {
        _builder.append("mod ");
        String _name = smod.getName();
        _builder.append(_name, "");
        _builder.append(" is");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _generateImportations = this.generateImportations(smod);
        _builder.append(_generateImportations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("endm");
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  /**
   * Given a System module, it generates all importations.
   * @params
   *  smod    the system module with none or more ModImportation objects
   */
  public CharSequence generateImportations(final SModule smod) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<ModElement> _els = smod.getEls();
      Iterable<ModImportation> _filter = Iterables.<ModImportation>filter(_els, ModImportation.class);
      for(final ModImportation imp : _filter) {
        {
          boolean _and = false;
          ImportationMode _mode = imp.getMode();
          boolean _equals = Objects.equal(_mode, ImportationMode.PROTECTING);
          if (!_equals) {
            _and = false;
          } else {
            ModExpression _imports = imp.getImports();
            _and = (_imports instanceof ModuleIdModExp);
          }
          if (_and) {
            _builder.append("pr ");
            ModExpression _imports_1 = imp.getImports();
            Module _module = ((ModuleIdModExp) _imports_1).getModule();
            String _name = _module.getName();
            _builder.append(_name, "");
            _builder.append(" .");
            _builder.newLineIfNotEmpty();
          } else {
            boolean _and_1 = false;
            ImportationMode _mode_1 = imp.getMode();
            boolean _equals_1 = Objects.equal(_mode_1, ImportationMode.INCLUDING);
            if (!_equals_1) {
              _and_1 = false;
            } else {
              ModExpression _imports_2 = imp.getImports();
              _and_1 = (_imports_2 instanceof ModuleIdModExp);
            }
            if (_and_1) {
              _builder.append("inc ");
              ModExpression _imports_3 = imp.getImports();
              Module _module_1 = ((ModuleIdModExp) _imports_3).getModule();
              String _name_1 = _module_1.getName();
              _builder.append(_name_1, "");
              _builder.append(" .");
              _builder.newLineIfNotEmpty();
            } else {
              boolean _and_2 = false;
              ImportationMode _mode_2 = imp.getMode();
              boolean _equals_2 = Objects.equal(_mode_2, ImportationMode.EXTENDING);
              if (!_equals_2) {
                _and_2 = false;
              } else {
                ModExpression _imports_4 = imp.getImports();
                _and_2 = (_imports_4 instanceof ModuleIdModExp);
              }
              if (_and_2) {
                _builder.append("ext ");
                ModExpression _imports_5 = imp.getImports();
                Module _module_2 = ((ModuleIdModExp) _imports_5).getModule();
                String _name_2 = _module_2.getName();
                _builder.append(_name_2, "");
                _builder.append(" .");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    return _builder;
  }
}
