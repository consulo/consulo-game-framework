package consulo.gameFramework.impl.internal.meta.projectView;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.gameFramework.meta.MetadataService;
import consulo.project.Project;
import consulo.project.ui.view.ProjectView;
import consulo.project.ui.view.ProjectViewPane;
import consulo.project.ui.view.ProjectViewPaneOptionProvider;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.Presentation;
import consulo.ui.ex.action.ToggleAction;
import consulo.util.dataholder.KeyWithDefaultValue;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2025-01-23
 */
@ExtensionImpl
public class MetadataProjectViewPaneOptionProvider extends ProjectViewPaneOptionProvider.BoolValue {
    public static final KeyWithDefaultValue<Boolean> KEY = KeyWithDefaultValue.create("show-meta-files", Boolean.FALSE);

    public final class ShowMetaFilesAction extends ToggleAction implements DumbAware {
        private ProjectViewPane myPane;

        private ShowMetaFilesAction(ProjectViewPane pane) {
            super("Show Metadata Files");
            myPane = pane;
        }

        @Override
        public boolean isSelected(@Nonnull AnActionEvent event) {
            return myPane.getUserData(KEY);
        }

        @Override
        public void setSelected(@Nonnull AnActionEvent event, boolean flag) {
            Boolean value = myPane.getUserData(KEY);
            assert value != null;
            if (value != flag) {
                myPane.putUserData(KEY, flag);
                myPane.updateFromRoot(true);
            }
        }

        @Override
        @RequiredUIAccess
        public void update(@Nonnull AnActionEvent e) {
            super.update(e);
            final Presentation presentation = e.getPresentation();
            Project project = e.getData(Project.KEY);
            if (project == null) {
                presentation.setVisible(false);
                return;
            }

            final ProjectView projectView = ProjectView.getInstance(project);
            if (projectView.getCurrentProjectViewPane() != myPane) {
                presentation.setVisible(false);
                return;
            }

            MetadataService metadataService = project.getInstance(MetadataService.class);
            presentation.setVisible(metadataService.isAvailable());
        }
    }

    @Nonnull
    @Override
    public KeyWithDefaultValue<Boolean> getKey() {
        return KEY;
    }

    @Override
    @RequiredUIAccess
    public void addToolbarActions(@Nonnull ProjectViewPane pane, @Nonnull DefaultActionGroup actionGroup) {
        if (pane instanceof ProjectViewPane) {
            actionGroup.add(new ShowMetaFilesAction(pane));
        }
    }
}
