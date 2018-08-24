package com.valleyforge.cdi.generated.tables;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Table(name = "ProjectResponseTables")
public class ProjectResponseTable extends Model {
    @Column(name = "type")
    public String type;

    @Column(name = "token")
    public String token;

    @Column(name = "msg")
    public String msg;

    @Column(name = "plist")
    public ArrayList<PListTable> pListTables = new ArrayList<>();

}
