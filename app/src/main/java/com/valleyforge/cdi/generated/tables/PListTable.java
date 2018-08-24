package com.valleyforge.cdi.generated.tables;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "PListTable")
public class PListTable extends Model {

    @Column(name="p_id")
    public String p_id;

    @Column(name="pname")
    public String pname;



    @Column(name="projectId")
    public String projectId;


    @Column(name="projectPercentage")
    public String projectPercentage;



    public PListTable() {
        super();
    }

    public PListTable(String p_id,String pname,String projectId,String projectPercentage) {
        super();
        this.p_id = p_id;
        this.pname = pname;
        this.projectId = projectId;
        this.projectPercentage =projectPercentage;
    }
}
