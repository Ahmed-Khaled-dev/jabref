package net.sf.jabref.gui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jabref.Globals;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileListTableModel;
import net.sf.jabref.logic.FieldChange;
import net.sf.jabref.logic.cleanup.Cleaner;
import net.sf.jabref.logic.util.io.FileUtil;
import net.sf.jabref.model.entry.BibEntry;


public class RelativePathsCleanup implements Cleaner {

    private final List<String> paths;

    public RelativePathsCleanup(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public List<FieldChange> cleanup(BibEntry entry) {
        if (!entry.hasField(Globals.FILE_FIELD)) {
            return new ArrayList<>();
        }
        String oldValue = entry.getField(Globals.FILE_FIELD);
        FileListTableModel flModel = new FileListTableModel();
        flModel.setContent(oldValue);
        if (flModel.getRowCount() == 0) {
            return new ArrayList<>();
        }
        boolean changed = false;
        for (int i = 0; i < flModel.getRowCount(); i++) {
            FileListEntry flEntry = flModel.getEntry(i);
            String oldFileName = flEntry.link;
            String newFileName = FileUtil
                    .shortenFileName(new File(oldFileName), paths)
                    .toString();
            if (!oldFileName.equals(newFileName)) {
                flModel.setEntry(i, new FileListEntry(flEntry.description, newFileName, flEntry.type));
                changed = true;
            }
        }
        if (changed) {
            String newValue = flModel.getStringRepresentation();
            assert(!oldValue.equals(newValue));
            entry.setField(Globals.FILE_FIELD, newValue);
            FieldChange change = new FieldChange(entry, Globals.FILE_FIELD, oldValue, newValue);
            return Collections.singletonList(change);
        }
        return new ArrayList<>();
    }

}
