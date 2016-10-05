# jr_simple_wrapper
Reduces design time of simple Jasper Reports templates: 

Simple template is just title and some columns of data.

With this tool, you can write report xml description in form like

'''xml
<?xml version="1.0" encoding="UTF-8"?>
<reportStub>
    <title>Title of the Report</title>
    <column title="First column' title" width="200" field="dataField1"/>
    <column title="Second column' title" width="100" field="dataField2"/>
    <column title="Third column' title" width="100" field="dataField3"/>
    <column title="Fourth column' title" width="150" field="dataField4"/>
</reportStub>
'''

and tool will expand it to a proper .jrxml
