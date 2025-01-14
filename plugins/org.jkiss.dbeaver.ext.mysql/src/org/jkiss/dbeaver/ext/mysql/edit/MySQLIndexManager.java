/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.mysql.edit;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ext.mysql.MySQLConstants;
import org.jkiss.dbeaver.ext.mysql.model.MySQLCatalog;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTable;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTableIndex;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTableIndexColumn;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.impl.DBSObjectCache;
import org.jkiss.dbeaver.model.impl.sql.edit.struct.SQLIndexManager;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.rdb.DBSIndexType;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.utils.CommonUtils;

import java.util.Map;

/**
 * MySQL index manager
 */
public class MySQLIndexManager extends SQLIndexManager<MySQLTableIndex, MySQLTable> {

    @Nullable
    @Override
    public DBSObjectCache<MySQLCatalog, MySQLTableIndex> getObjectsCache(MySQLTableIndex object)
    {
        return object.getTable().getContainer().getIndexCache();
    }

    @Override
    protected MySQLTableIndex createDatabaseObject(
        DBRProgressMonitor monitor, DBECommandContext context, final MySQLTable parent,
        Object from, Map<String, Object> options)
    {
        return new MySQLTableIndex(
            parent,
            false,
            null,
            DBSIndexType.OTHER,
            null,
            false);
    }

    @Override
    protected String getDropIndexPattern(MySQLTableIndex index)
    {
        return "ALTER TABLE " + PATTERN_ITEM_TABLE + " DROP INDEX " + PATTERN_ITEM_INDEX_SHORT; //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void appendIndexType(MySQLTableIndex index, StringBuilder decl) {
        DBSIndexType indexType = index.getIndexType();
        if (indexType != MySQLConstants.INDEX_TYPE_FULLTEXT) {
            decl.append(" USING ").append(indexType.getId());
        }
    }

    protected void appendIndexModifiers(MySQLTableIndex index, StringBuilder decl) {
        if (index.getIndexType() == MySQLConstants.INDEX_TYPE_FULLTEXT) {
            decl.append(" FULLTEXT");
        } else {
            super.appendIndexModifiers(index, decl);
        }
    }

    protected void appendIndexColumnModifiers(DBRProgressMonitor monitor, StringBuilder decl, DBSTableIndexColumn indexColumn) {
        final String subPart = ((MySQLTableIndexColumn) indexColumn).getSubPart();
        if (!CommonUtils.isEmpty(subPart)) {
            decl.append(" (").append(subPart).append(")");
        }
        if (!indexColumn.isAscending()) {
            decl.append(" DESC"); //$NON-NLS-1$
        }
    }

}
