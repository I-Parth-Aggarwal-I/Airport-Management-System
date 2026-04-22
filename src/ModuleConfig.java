import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ModuleConfig {

    public final String   title;
    public final String   icon;
    public final String   tableName;       
    public final String   primaryKey;      
    public final String   selectQuery;     
    public final String[] columnHeaders;   
    public final String[] columnDbNames;   
    public final String[] editableColumns; 
    public final String[] editableHeaders; 
    public final Color    color;
    public final boolean  canAdd;
    public final boolean  canEdit;
    public final boolean  canDelete;

    public ModuleConfig(String title, String icon, String tableName, String primaryKey,
                        String selectQuery,
                        String[] columnHeaders, String[] columnDbNames,
                        String[] editableColumns, String[] editableHeaders,
                        Color color,
                        boolean canAdd, boolean canEdit, boolean canDelete) {
        this.title           = title;
        this.icon            = icon;
        this.tableName       = tableName;
        this.primaryKey      = primaryKey;
        this.selectQuery     = selectQuery;
        this.columnHeaders   = columnHeaders;
        this.columnDbNames   = columnDbNames;
        this.editableColumns = editableColumns;
        this.editableHeaders = editableHeaders;
        this.color           = color;
        this.canAdd          = canAdd;
        this.canEdit         = canEdit;
        this.canDelete       = canDelete;
    }

    public String[] getBadges() {
        List<String> b = new ArrayList<>();
        if (canAdd)    b.add("+ Add");
        if (canEdit)   b.add("✎ Edit");
        if (canDelete) b.add("✕ Del");
        if (b.isEmpty()) b.add("View Only");
        return b.toArray(new String[0]);
    }

    public int getPrimaryKeyIndex() {
        for (int i = 0; i < columnDbNames.length; i++)
            if (columnDbNames[i].equalsIgnoreCase(primaryKey)) return i;
        return 0;
    }
}
