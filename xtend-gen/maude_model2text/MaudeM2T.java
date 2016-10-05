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

import Maude.BooleanCond;
import Maude.Condition;
import Maude.Constant;
import Maude.Equation;
import Maude.ImportationMode;
import Maude.MatchingCond;
import Maude.MaudePackage;
import Maude.MaudeSpec;
import Maude.MaudeTopEl;
import Maude.ModElement;
import Maude.ModExpression;
import Maude.ModImportation;
import Maude.Module;
import Maude.ModuleIdModExp;
import Maude.Operation;
import Maude.RecTerm;
import Maude.Rule;
import Maude.SModule;
import Maude.Sort;
import Maude.SubsortRel;
import Maude.Term;
import Maude.Type;
import Maude.Variable;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import maude_model2text.Util;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

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
      final Function1<Module, Boolean> _function = (Module m) -> {
        Set<String> _skippedModules = Util.skippedModules();
        String _name = m.getName();
        boolean _contains = _skippedModules.contains(_name);
        return Boolean.valueOf((!_contains));
      };
      Iterable<SModule> _filter_1 = IterableExtensions.<SModule>filter(_filter, _function);
      for(final SModule smod : _filter_1) {
        _builder.append("mod ");
        String _name = smod.getName();
        _builder.append(_name, "");
        _builder.append(" is");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_1 = smod.getEls();
        Iterable<ModImportation> _filter_2 = Iterables.<ModImportation>filter(_els_1, ModImportation.class);
        CharSequence _generateImportations = this.generateImportations(_filter_2);
        _builder.append(_generateImportations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_2 = smod.getEls();
        Iterable<Sort> _filter_3 = Iterables.<Sort>filter(_els_2, Sort.class);
        CharSequence _generateSortDeclarations = this.generateSortDeclarations(_filter_3);
        _builder.append(_generateSortDeclarations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_3 = smod.getEls();
        Iterable<SubsortRel> _filter_4 = Iterables.<SubsortRel>filter(_els_3, SubsortRel.class);
        CharSequence _generateSubsortDeclarations = this.generateSubsortDeclarations(_filter_4);
        _builder.append(_generateSubsortDeclarations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_4 = smod.getEls();
        Iterable<Operation> _filter_5 = Iterables.<Operation>filter(_els_4, Operation.class);
        CharSequence _generateOperations = this.generateOperations(_filter_5);
        _builder.append(_generateOperations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_5 = smod.getEls();
        Iterable<Equation> _filter_6 = Iterables.<Equation>filter(_els_5, Equation.class);
        CharSequence _generateEquations = this.generateEquations(_filter_6);
        _builder.append(_generateEquations, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        EList<ModElement> _els_6 = smod.getEls();
        Iterable<Rule> _filter_7 = Iterables.<Rule>filter(_els_6, Rule.class);
        CharSequence _generateRules = this.generateRules(_filter_7);
        _builder.append(_generateRules, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("endm");
        _builder.newLine();
      }
    }
    return _builder;
  }
  
  /**
   * Given a Maude module, it generates all importations.
   * @params
   *  mod    the Module with none or more ModImportation objects
   */
  public CharSequence generateImportations(final Iterable<ModImportation> importations) {
    StringConcatenation _builder = new StringConcatenation();
    {
      int _size = IterableExtensions.size(importations);
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Importations");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      for(final ModImportation imp : importations) {
        {
          if ((Objects.equal(imp.getMode(), ImportationMode.PROTECTING) && (imp.getImports() instanceof ModuleIdModExp))) {
            _builder.append("pr ");
            ModExpression _imports = imp.getImports();
            Module _module = ((ModuleIdModExp) _imports).getModule();
            String _name = _module.getName();
            _builder.append(_name, "");
            _builder.append(" .");
            _builder.newLineIfNotEmpty();
          } else {
            if ((Objects.equal(imp.getMode(), ImportationMode.INCLUDING) && (imp.getImports() instanceof ModuleIdModExp))) {
              _builder.append("inc ");
              ModExpression _imports_1 = imp.getImports();
              Module _module_1 = ((ModuleIdModExp) _imports_1).getModule();
              String _name_1 = _module_1.getName();
              _builder.append(_name_1, "");
              _builder.append(" .");
              _builder.newLineIfNotEmpty();
            } else {
              if ((Objects.equal(imp.getMode(), ImportationMode.EXTENDING) && (imp.getImports() instanceof ModuleIdModExp))) {
                _builder.append("ext ");
                ModExpression _imports_2 = imp.getImports();
                Module _module_2 = ((ModuleIdModExp) _imports_2).getModule();
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
    {
      int _size_1 = IterableExtensions.size(importations);
      boolean _greaterThan_1 = (_size_1 > 0);
      if (_greaterThan_1) {
        _builder.append("---- <end> Importations");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  /**
   * Given a Maude module, it generates all sort declarations.
   * @params
   *  mod    the Module with none or more sort objects
   */
  public CharSequence generateSortDeclarations(final Iterable<Sort> sorts) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(sorts);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Sort declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      for(final Sort sort : sorts) {
        _builder.append("sort ");
        String _name = sort.getName();
        _builder.append(_name, "");
        _builder.append(" .");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      boolean _isEmpty_1 = IterableExtensions.isEmpty(sorts);
      boolean _not_1 = (!_isEmpty_1);
      if (_not_1) {
        _builder.append("---- <end> Sort declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  /**
   * Given a Maude module, it generates all subsort declarations.
   * @params
   *  mod    the Module with none or more sort objects
   */
  public CharSequence generateSubsortDeclarations(final Iterable<SubsortRel> ssorts) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(ssorts);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Subsort declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      for(final SubsortRel ssort : ssorts) {
        _builder.append("subsort ");
        EList<Sort> _subsorts = ssort.getSubsorts();
        Sort _get = _subsorts.get(0);
        String _name = _get.getName();
        _builder.append(_name, "");
        _builder.append(" < ");
        EList<Sort> _supersorts = ssort.getSupersorts();
        Sort _get_1 = _supersorts.get(0);
        String _name_1 = _get_1.getName();
        _builder.append(_name_1, "");
        _builder.append(" .");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      boolean _isEmpty_1 = IterableExtensions.isEmpty(ssorts);
      boolean _not_1 = (!_isEmpty_1);
      if (_not_1) {
        _builder.append("---- <end> Subsort declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence generateOperations(final Iterable<Operation> operations) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(operations);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Operation declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      for(final Operation op : operations) {
        _builder.append("op ");
        String _name = op.getName();
        _builder.append(_name, "");
        _builder.append(" : ");
        EList<Type> _arity = op.getArity();
        CharSequence _printArity = this.printArity(_arity);
        _builder.append(_printArity, "");
        _builder.append("-> ");
        Type _coarity = op.getCoarity();
        CharSequence _printCoarity = this.printCoarity(_coarity);
        _builder.append(_printCoarity, "");
        _builder.append(" ");
        {
          EList<String> _atts = op.getAtts();
          boolean _isEmpty_1 = _atts.isEmpty();
          boolean _not_1 = (!_isEmpty_1);
          if (_not_1) {
            _builder.append("[");
            EList<String> _atts_1 = op.getAtts();
            CharSequence _printAtts = this.printAtts(_atts_1);
            _builder.append(_printAtts, "");
            _builder.append("] ");
          }
        }
        _builder.append(".");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      boolean _isEmpty_2 = IterableExtensions.isEmpty(operations);
      boolean _not_2 = (!_isEmpty_2);
      if (_not_2) {
        _builder.append("---- <end> Operation declarations");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printAtts(final EList<String> list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      for(final String a : list) {
        _builder.append(a, "");
        {
          int _size = list.size();
          int _minus = (_size - 1);
          String _get = list.get(_minus);
          boolean _equals = a.equals(_get);
          boolean _not = (!_equals);
          if (_not) {
            _builder.append(" ");
          }
        }
      }
    }
    return _builder;
  }
  
  public CharSequence printArity(final EList<Type> list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final Type t : list) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(" ", "");
        }
        String _name = t.getName();
        _builder.append(_name, "");
      }
      if (_hasElements) {
        _builder.append(" ", "");
      }
    }
    return _builder;
  }
  
  public CharSequence printCoarity(final Type type) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = type.getName();
    _builder.append(_name, "");
    return _builder;
  }
  
  public CharSequence generateEquations(final Iterable<Equation> equations) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(equations);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Equations");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      boolean _hasElements = false;
      for(final Equation eq : equations) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n", "");
        }
        {
          EList<Condition> _conds = eq.getConds();
          boolean _isEmpty_1 = _conds.isEmpty();
          if (_isEmpty_1) {
            CharSequence _printNoConditionalEq = this.printNoConditionalEq(eq);
            _builder.append(_printNoConditionalEq, "");
          } else {
            CharSequence _printConditionalEq = this.printConditionalEq(eq);
            _builder.append(_printConditionalEq, "");
          }
        }
      }
    }
    _builder.newLineIfNotEmpty();
    {
      boolean _isEmpty_2 = IterableExtensions.isEmpty(equations);
      boolean _not_1 = (!_isEmpty_2);
      if (_not_1) {
        _builder.append("---- <end> Equations");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printConditionalEq(final Equation equation) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("ceq ");
    {
      if (((!Objects.equal(equation.getLabel(), null)) && (!equation.getLabel().equals("")))) {
        _builder.append("[");
        String _label = equation.getLabel();
        _builder.append(_label, "");
        _builder.append("] : ");
      }
    }
    Term _lhs = equation.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("= ");
    Term _rhs = equation.getRhs();
    CharSequence _printTerm_1 = this.printTerm(_rhs);
    _builder.append(_printTerm_1, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("if ");
    EList<Condition> _conds = equation.getConds();
    CharSequence _printConditions = this.printConditions(_conds);
    _builder.append(_printConditions, "  ");
    _builder.append(" ");
    {
      EList<String> _atts = equation.getAtts();
      boolean _isEmpty = _atts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("[");
        EList<String> _atts_1 = equation.getAtts();
        CharSequence _printAtts = this.printAtts(_atts_1);
        _builder.append(_printAtts, "  ");
        _builder.append("] ");
      }
    }
    _builder.append(".");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printConditions(final EList<Condition> conditions) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _hasElements = false;
      for(final Condition cond : conditions) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n/\\ ", "");
        }
        CharSequence _printCond = this.printCond(cond);
        _builder.append(_printCond, "");
      }
    }
    return _builder;
  }
  
  public CharSequence printCond(final Condition condition) {
    CharSequence _xifexpression = null;
    if ((condition instanceof MatchingCond)) {
      _xifexpression = this.printMatchingCond(((MatchingCond) condition));
    } else {
      CharSequence _xifexpression_1 = null;
      if ((condition instanceof BooleanCond)) {
        _xifexpression_1 = this.printBooleanCond(((BooleanCond) condition));
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  public CharSequence printMatchingCond(final MatchingCond cond) {
    StringConcatenation _builder = new StringConcatenation();
    Term _lhs = cond.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    _builder.append(" := ");
    Term _rhs = cond.getRhs();
    CharSequence _printTerm_1 = this.printTerm(_rhs);
    _builder.append(_printTerm_1, "");
    return _builder;
  }
  
  public CharSequence printBooleanCond(final BooleanCond cond) {
    StringConcatenation _builder = new StringConcatenation();
    Term _lhs = cond.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    return _builder;
  }
  
  public CharSequence printNoConditionalEq(final Equation equation) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("eq ");
    {
      if (((!Objects.equal(equation.getLabel(), null)) && (!equation.getLabel().equals("")))) {
        _builder.append("[");
        String _label = equation.getLabel();
        _builder.append(_label, "");
        _builder.append("] : ");
      }
    }
    Term _lhs = equation.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("= ");
    Term _rhs = equation.getRhs();
    CharSequence _printTerm_1 = this.printTerm(_rhs);
    _builder.append(_printTerm_1, "  ");
    _builder.append(" ");
    {
      EList<String> _atts = equation.getAtts();
      boolean _isEmpty = _atts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("[");
        EList<String> _atts_1 = equation.getAtts();
        CharSequence _printAtts = this.printAtts(_atts_1);
        _builder.append(_printAtts, "  ");
        _builder.append("] ");
      }
    }
    _builder.append(".");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printTerm(final Term term) {
    CharSequence _xifexpression = null;
    if ((term instanceof Variable)) {
      _xifexpression = this.printVariable(((Variable)term));
    } else {
      CharSequence _xifexpression_1 = null;
      if ((term instanceof Constant)) {
        _xifexpression_1 = this.printConstant(((Constant)term));
      } else {
        CharSequence _xifexpression_2 = null;
        if ((term instanceof RecTerm)) {
          _xifexpression_2 = this.printRecTerm(((RecTerm)term));
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  public CharSequence printRecTerm(final RecTerm rt) {
    StringConcatenation _builder = new StringConcatenation();
    String _op = rt.getOp();
    _builder.append(_op, "");
    {
      EList<Term> _args = rt.getArgs();
      boolean _hasElements = false;
      for(final Term t : _args) {
        if (!_hasElements) {
          _hasElements = true;
          _builder.append("(", "");
        } else {
          _builder.appendImmediate(",", "");
        }
        Object _printTerm = this.printTerm(((Term) t));
        _builder.append(_printTerm, "");
      }
      if (_hasElements) {
        _builder.append(")", "");
      }
    }
    return _builder;
  }
  
  public CharSequence printConstant(final Constant constant) {
    StringConcatenation _builder = new StringConcatenation();
    String _op = constant.getOp();
    _builder.append(_op, "");
    return _builder;
  }
  
  public CharSequence printVariable(final Variable variable) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = variable.getName();
    _builder.append(_name, "");
    _builder.append(":");
    Type _type = variable.getType();
    String _name_1 = _type.getName();
    _builder.append(_name_1, "");
    return _builder;
  }
  
  public CharSequence generateRules(final Iterable<Rule> rules) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(rules);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append(" ");
        _builder.newLine();
        _builder.append("---- <begin> Rules");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      boolean _hasElements = false;
      for(final Rule rl : rules) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate("\n", "");
        }
        {
          EList<Condition> _conds = rl.getConds();
          boolean _isEmpty_1 = _conds.isEmpty();
          if (_isEmpty_1) {
            CharSequence _printNoConditionalRule = this.printNoConditionalRule(rl);
            _builder.append(_printNoConditionalRule, "");
          } else {
            CharSequence _printConditionalRule = this.printConditionalRule(rl);
            _builder.append(_printConditionalRule, "");
          }
        }
      }
    }
    _builder.newLineIfNotEmpty();
    {
      boolean _isEmpty_2 = IterableExtensions.isEmpty(rules);
      boolean _not_1 = (!_isEmpty_2);
      if (_not_1) {
        _builder.append("---- <end> Rules");
      }
    }
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printNoConditionalRule(final Rule rl) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("rl ");
    {
      if (((!Objects.equal(rl.getLabel(), null)) && (!rl.getLabel().equals("")))) {
        _builder.append("[");
        String _label = rl.getLabel();
        _builder.append(_label, "");
        _builder.append("] : ");
      }
    }
    Term _lhs = rl.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("=> ");
    Term _rhs = rl.getRhs();
    CharSequence _printTerm_1 = this.printTerm(_rhs);
    _builder.append(_printTerm_1, "  ");
    _builder.append(" ");
    {
      EList<String> _atts = rl.getAtts();
      boolean _isEmpty = _atts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("[");
        EList<String> _atts_1 = rl.getAtts();
        CharSequence _printAtts = this.printAtts(_atts_1);
        _builder.append(_printAtts, "  ");
        _builder.append("] ");
      }
    }
    _builder.append(".");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence printConditionalRule(final Rule rl) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("crl ");
    {
      if (((!Objects.equal(rl.getLabel(), null)) && (!rl.getLabel().equals("")))) {
        _builder.append("[");
        String _label = rl.getLabel();
        _builder.append(_label, "");
        _builder.append("] : ");
      }
    }
    Term _lhs = rl.getLhs();
    CharSequence _printTerm = this.printTerm(_lhs);
    _builder.append(_printTerm, "");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("=> ");
    Term _rhs = rl.getRhs();
    CharSequence _printTerm_1 = this.printTerm(_rhs);
    _builder.append(_printTerm_1, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("if ");
    EList<Condition> _conds = rl.getConds();
    CharSequence _printConditions = this.printConditions(_conds);
    _builder.append(_printConditions, "  ");
    _builder.append(" ");
    {
      EList<String> _atts = rl.getAtts();
      boolean _isEmpty = _atts.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("[");
        EList<String> _atts_1 = rl.getAtts();
        CharSequence _printAtts = this.printAtts(_atts_1);
        _builder.append(_printAtts, "  ");
        _builder.append("] ");
      }
    }
    _builder.append(".");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
