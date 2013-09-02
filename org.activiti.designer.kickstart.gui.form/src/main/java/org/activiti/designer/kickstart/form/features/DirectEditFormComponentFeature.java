package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.command.FormPropertyDefinitionModelUpdater;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Frederik Heremans
 */
public class DirectEditFormComponentFeature extends AbstractDirectEditingFeature implements ICustomUndoableFeature {

  protected FormPropertyDefinitionModelUpdater updater;
  
  public DirectEditFormComponentFeature(IFeatureProvider fp) {
    super(fp);
  }

  public int getEditingType() {
    return TYPE_TEXT;
  }

  @Override
  public boolean canDirectEdit(IDirectEditingContext context) {
    PictogramElement pe = context.getPictogramElement();
    Object bo = getBusinessObjectForPictogramElement(pe);
    return bo instanceof FormPropertyDefinition;
  }

  public String getInitialValue(IDirectEditingContext context) {
    // Return the current name of the EClass
    PictogramElement pe = context.getPictogramElement();
    FormPropertyDefinition definition = (FormPropertyDefinition) getBusinessObjectForPictogramElement(pe);
    return definition.getName();
  }

  @Override
  public String checkValueValid(String value, IDirectEditingContext context) {
    if (value.length() < 1) {
      return "The label should be at least one character long";
    }
    return null;
  }

  public void setValue(String value, IDirectEditingContext context) {
    FormPropertyDefinitionModelUpdater updater = getUpdater(context.getPictogramElement(), true);
    if (updater != null) {
      // Set the new value in the updateable definition
      updater.getUpdatableBusinessObject().setName(value);
      updater.doUpdate();
    }
  }

  @Override
  public void undo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    FormPropertyDefinitionModelUpdater updater = getUpdater(((IDirectEditingContext) context).getPictogramElement(), false);
    if (updater != null) {
      updater.doUndo();
    }
  }

  @Override
  public boolean canRedo(IContext context) {
    return true;
  }

  @Override
  public void redo(IContext context) {
    // Use an updater that does not trigger a diagram update, as this is done by the 
    // undo of the base class
    FormPropertyDefinitionModelUpdater updater = getUpdater(((IDirectEditingContext) context).getPictogramElement(), false);
    if (updater != null) {
      updater.doUpdate();
    }
  }

  protected FormPropertyDefinitionModelUpdater getUpdater(PictogramElement pe, boolean includeDiagramUpdate) {
    if(updater == null) {
      FormPropertyDefinition propDef = (FormPropertyDefinition) getBusinessObjectForPictogramElement(pe);
      if (propDef != null) {
        if(includeDiagramUpdate) {
          updater = new FormPropertyDefinitionModelUpdater(propDef, pe, getFeatureProvider());
        } else {
          updater = new FormPropertyDefinitionModelUpdater(propDef, null, getFeatureProvider());
        }
      }
    }
    return updater;
  }
}