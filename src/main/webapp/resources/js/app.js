var Log = {
  elem: false,
  info: function(text){
	  if (!this.elem) this.elem = document.getElementById('log');
	  this.elem.innerHTML       = text;
	  this.elem.style.left      = (550 - this.elem.offsetWidth / 2) + 'px';
  },
  error: function(text){
	  if (!this.elem) this.elem = document.getElementById('log');
	  this.elem.innerHTML       = text;
	  this.elem.style.left      = (550 - this.elem.offsetWidth / 2) + 'px';  
  }
};

const ObjectManager = {
	lastClicked: null,
	getLastClicked: function(){ return this.lastClicked; },
	setLastClicked: function(lastClicked){ this.lastClicked = lastClicked; }
}

var st = null
function initTree(json){
    st = new $jit.ST({
   		injectInto: 'infovis',
   		background:false,
   		orientation: 'top',
   	    duration: 800,
        transition: $jit.Trans.Quart.easeInOut,
        levelsToShow: 5,
        levelDistance: 50,
        
        Node: {
            height: 20,
            width: 80,
            type: 'rectangle',
            overridable: true,
            CanvasStyles:{
            	shadowColor: 'black',
                shadowBlur: 10,
                shadowOffsetY: 10
            }
        },
        
        Edge: {
            type: 'arrow',
            color: '#A7B6FF',
            overridable: true,
            CanvasStyles:{
            	shadowColor: 'black',
                shadowBlur: 10,
                shadowOffsetY: 10
            }
        },
        
        onBeforeCompute: function(node){
        	if(node) Log.info("loading " + node.name);
        },
        
        onAfterCompute: function(){
            Log.info("done");
        },
        
        onCreateLabel: function(label, node){
        	
        	Ext.EventManager.on(label, 'contextmenu', teste);
        	function teste(e,t){
        		window.getSelection().removeAllRanges();
        		ObjectManager.setLastClicked(node);
        		
        		e.stopEvent();
        		contextMenu.showAt(e.getXY());
        	}
        	
            label.id          = node.id;            
            label.innerHTML   = node.name;
            
            var style         = label.style;
            style.width       = 80 + 'px';
            style.height      = 20 + 'px';
            style.color       = 'white';
            style.textAlign   = 'center';
            style.fontWeight  = 'bold';
            style.fontFamily  = 'tahoma,arial,verdana,sans-serif';
            style.fontSize    = '11px';
            style.overflow    = "hidden";
            style.cursor      = 'pointer';
        },
        
        Events: {  
            enable: true,
            
            onClick: function(node, eventInfo, e){
            	if(node){
            		ObjectManager.setLastClicked(node);
            		st.onClick(node.id);
            	}
            }
        },
        
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
            if (node.selected) {
                node.data.$color = '#23A4FF';
            }
            else {
                delete node.data.$color;
                //if the node belongs to the last plotted level
                if(!node.anySubnode("exist")) {
                    var count = 0;
                    node.eachSubnode(function(n) { count++; });
                    //assign a node color based on how many children it has
                    node.data.$color = ['#6D89D5', '#476DD5', '#133CAC', '#2B4281', '#062270', '#090974'][count];                    
                }
            }
        },
        
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = '#23A4FF';
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });
    
    try{
    	if(json.id == null) throw "Invalid server data."
    	st.loadJSON(json);
    	st.compute();
    	st.geom.translate(new $jit.Complex(0, -300), "current");//optional: make a translation of the tree
    	st.onClick(st.root);
    }
    catch(e){
    	alert("Was not possible to load the tree. " + e)
    }
    
}

Ext.application({
    name: 'Brain',
    launch: function() {
        Ext.create('Ext.container.Viewport', {
            layout: 'border',
            items: [
                {
			        region: 'north',
			        html: '<h1>Brain</h1>',
			        border: true,
			        margins: '5 5 5 5'
			    }, {
			        region: 'west',
			        title: 'Help',
			        width: 300,
			        margins: '0 5 0 5'
			        // could use a TreePanel or AccordionLayout for navigational items
			    }, {
			        region: 'south',
			        contentEl: 'log',
			        height: 30,
			        minHeight: 30,
			        margins: '5 5 5 5'
			    }, {
			        region: 'east',
			        width: 300,
			        margins: '0 5 0 5',
			        items:[
				        Ext.create('Ext.tab.Panel', {
					    	region: 'center',
					    	border:false,
					        items: [
				                {
				                	title: 'Properties',
				                	disabled: true,
			                		tabConfig: {
						                tooltip: 'The properties of the selected element.'
						            }
						        }, 
						        {
						            title: 'Chat',
						            tabConfig: {
						                tooltip: 'A chat to test the knowledge base.'
						            }
						        },
						        {
						            title: 'Utilities',
						            disabled: true
						        }
					        ]
					    })
				    ]
			    },
			    Ext.create('Ext.tab.Panel', {
			    	region: 'center',
			        items: [
		                {
		                	title: 'Knowledge Base',
		                	contentEl: 'theTree'
				        }, 
				        {
				            title: 'Bots',
				            disabled: true,
				            tabConfig: {
				                tooltip: 'AIML Bots configurations set'
				            }
				        }
			        ],
			        tbar: [
							{
								text:'Apply',
								tooltip:'Apply the new Knowledge Base to the bot.',
								handler: function(){
									applyKnowledge();
								}
							}
						]
			    })
            ]
        });
    }
});

var createKnowledgeForm = Ext.create('Ext.window.Window', {
    title:       'Add a Knowledge',
    closeAction: 'hide',
    modal:       true,
    closable:    true,
    resizable:	 false,
    height:      200,
    width:       400,
    layout:      'fit',
    items:       {
    	xtype:     'panel',
    	border:    false,
    	contentEl: 'createKnowledgeWindow'
    },
	listeners: {
		'beforehide':function(window){
			document.getElementById("createKnowledgeNameInput").form.reset();
			document.getElementById("createKnowledgeWindowStatus").innerHTML='';
		},
    	'beforeshow':function(window){
    		document.getElementById("into").value=ObjectManager.lastClicked.id;
    	}
	}
});
var updateKnowledgeForm = Ext.create('Ext.window.Window', {
	title:       'Rename the Knowledge',
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      200,
	width:       400,
	layout:      'fit',
	items:       {
		xtype:     'panel',
		border:    false,
		contentEl: 'updateKnowledgeWindow'
	},
	listeners: {
		'beforehide':function(window){
			document.getElementById("updateKnowledgeNameInput").form.reset();
			document.getElementById("updateKnowledgeWindowStatus").innerHTML='';
		},
		'beforeshow':function(window){
			document.getElementById("updateKnowledgeNameInput").value=ObjectManager.lastClicked.name;
			document.getElementById("whatToRename").value=ObjectManager.lastClicked.id;
		}
	}
});
var deleteKnowledgeForm = Ext.create('Ext.window.Window', {
	title:       'Delete the Knowledge',
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      200,
	width:       400,
	layout:      'fit',
	items:       {
		xtype:     'panel',
		border:    false,
		contentEl: 'deleteKnowledgeWindow'
	},
	listeners: {
		'beforehide':function(window){
			document.getElementById("whatKnowledgeToDelete").form.reset();
			document.getElementById("deleteKnowledgeWindowStatus").innerHTML='';
		},
		'beforeshow':function(window){
			document.getElementById("whatKnowledgeToDelete").value=ObjectManager.lastClicked.id;
		}
	}
});

var informationExtWrapper = {
	panel: Ext.create('Ext.grid.Panel', {
		   region: 'north',
		   margins: '5 5 5 5',
		   height: 205,
		   title: 'Informations',
		   store: Ext.create('Ext.data.Store', {
			   config:{
				   sortOnLoad:true,
			   },
			   sorters: [{
			         property: 'name',
			         direction: 'ASC'
			     }],
			    storeId:'informationStore',
			    fields:['id', 'name', 'knowledgeId'],
			    data:[]
			}),
		   tbar: [
				{
					  text: 'Create',
					  handler:function(){
						  informationExtWrapper.formPanel.show();
					  }
				},
				{
					  text: 'Update',
					  handler:function(){
						  selectedItem = informationExtWrapper.panel.getSelectionModel().getLastSelected().data
						  Ext.getElementById("informationId").value= selectedItem.id;
						  Ext.getElementById("informationNameInput").value= selectedItem.name;
						  Ext.getElementById("informationKnowledgeId").value=selectedItem.knowledgeId;
						  informationExtWrapper.formPanel.show();
					  }
				},
				{
					  text: 'Delete',
					  handler:function(){
						  informationExtWrapper.deleteFormPanel.show();
					  }
				}
			],
		    columns: [{ text: 'Name',  dataIndex: 'name', width:'100%'}],
		    listeners:{
		    	select:function( theGrid, record, index, eOpts ){
		    		teachingExtWrapper.panel.getSelectionModel().clearSelections();
		    		teachingExtWrapper.panel.setDisabled(false)
		    		teachingExtWrapper.panel.setTitle("Teachings of " + record.data.name)
		    		teachingExtWrapper.panel.store.loadData([]);// TODO: search a better way to clear the grid.
					teachingLoadMask.show();
		 		    loadTeachings();
		    	},
				deselect:function( record, index, eOpts ){
					teachingExtWrapper.panel.setDisabled(true)
				}
		    }
	}),
	
	formPanel: Ext.create('Ext.window.Window', {
		title:       'Create the Information',
		closeAction: 'hide',
		modal:       true,
		closable:    true,
		resizable:	 false,
		height:      200,
		width:       400,
		layout:      'fit',
		items:       {
			xtype:     'panel',
			border:    false,
			contentEl: 'informationForm'
		},
		listeners: {
			'beforehide':function(window){
				document.getElementById("informationId").value='';
				document.getElementById("informationNameInput").value='';
				document.getElementById("informationFormStatus").innerHTML='';
			},
			'beforeshow':function(window){
				document.getElementById("informationKnowledgeId").value=ObjectManager.lastClicked.id;
			}
		}
	}),
	
	deleteFormPanel: Ext.create('Ext.window.Window', {
		title:       'Delete the Information',
		closeAction: 'hide',
		modal:       true,
		closable:    true,
		resizable:	 false,
		height:      200,
		width:       400,
		layout:      'fit',
		items:       {
			xtype:     'panel',
			border:    false,
			contentEl: 'deleteInformationForm'
		},
		listeners: {
			'beforehide':function(window){
				document.getElementById("whatInformationToDelete").value='';
				document.getElementById("deleteInformationFormStatus").innerHTML='';
			},
			'beforeshow':function(window){
				document.getElementById("whatInformationToDelete").value=informationExtWrapper.panel.getSelectionModel().getLastSelected().data.id;
			}
		}
	})
}

var teachingExtWrapper = {
	panel: Ext.create('Ext.grid.Panel', {
		disabled : true,
		   region: 'center',
		   layout: 'fit',
		   margins: '0 5 5 5',
		   title: 'Teachings',
		   store: Ext.create('Ext.data.Store', {
			   config:{
				   sortOnLoad:true,
			   },
			   sorters: [{
			         property: 'name',
			         direction: 'ASC'
			     }],
			    storeId:'teachingStore',
			    fields:['id', 'informationId', 'whenTheUserSays', 'respondingTo', 'memorize', 'say'],
			    data:[]
			}),
		   tbar: [
		          {
		        	  text: 'Create',
					  handler:function(){
						  teachingExtWrapper.resetFormPanel();
						  teachingExtWrapper.formPanel.show();
					  }
		          },
		          {
		        	  text: 'Update',
					  handler:function(){
						  if(teachingExtWrapper.panel.getSelectionModel().getLastSelected() != null){
							  teachingExtWrapper.prepareUpdateFormPanel();
							  teachingExtWrapper.formPanel.show();
						  }
					  }
		          },
		          {
		        	  text: 'Delete',
					  handler:function(){
						  if(teachingExtWrapper.panel.getSelectionModel().getLastSelected() != null){
							  teachingExtWrapper.deleteFormPanel.show();
						  }
					  }
		          }
		          ],
		   columns: [
                    { text: 'When the user says',  dataIndex: 'whenTheUserSays', width:280},
                    { text: 'Responding to',  dataIndex: 'respondingTo', width:200 },
                    { text: 'Memorize',  dataIndex: 'memorize', width:100},
                    { text: 'Say',  dataIndex: 'say', width:280 },
           ],
           listeners:{
        	   select:function( theGrid, record, index, eOpts ){
        	   },
        	   deselect:function( record, index, eOpts ){
        	   },
        	   itemdblclick:function( record, index, eOpts ){
        		   teachingExtWrapper.prepareUpdateFormPanel();
        		   teachingExtWrapper.formPanel.show();
        	   }
           },
           height: 200
	   }),
	   
	   formPanel: Ext.create('Ext.window.Window', {
			title:       'Create the Teaching',
			closeAction: 'hide',
			modal:       true,
			closable:    true,
			resizable:	 false,
			height:      410,
			width:       500,
			layout:      'fit',
			items:       {
				xtype:     'panel',
				border:    false,
				contentEl: 'teachingForm'
			},
			listeners: {
				'beforehide':function(window){
					teachingExtWrapper.resetFormPanel();
				},
				'beforeshow':function(window){
					document.getElementById("teachingInformationId").value=informationExtWrapper.panel.getSelectionModel().getLastSelected().data.id;
				}
			}
		}),
		
		resetFormPanel : function(){
		   document.getElementById("teachingId").value='';
		   document.getElementById("teachingInformationId").value='';
		   document.getElementById("whenTheUserSaysInput").value='';
		   document.getElementById("whenTheUserSaysInput").value='';
		   document.getElementById("respondingToInput").value='';
		   document.getElementById("memorizeInput").value='';
		   document.getElementById("sayInput").value='';
		   document.getElementById("teachingFormStatus").innerHTML='';
	   },
	   
	   prepareUpdateFormPanel :  function(){
		   selectedItem = teachingExtWrapper.panel.getSelectionModel().getLastSelected().data
		   document.getElementById("teachingId").value            = selectedItem.id;
		   document.getElementById("teachingInformationId").value = selectedItem.teachingInformationId;
		   document.getElementById("whenTheUserSaysInput").value  = selectedItem.whenTheUserSays;
		   document.getElementById("respondingToInput").value     = selectedItem.respondingTo;
		   document.getElementById("memorizeInput").value         = selectedItem.memorize;
		   document.getElementById("sayInput").value              = selectedItem.say;
		   document.getElementById("teachingFormStatus").innerHTML='';
	   },
		
		deleteFormPanel: Ext.create('Ext.window.Window', {
			title:       'Delete the Teaching',
			closeAction: 'hide',
			modal:       true,
			closable:    true,
			resizable:	 false,
			height:      200,
			width:       400,
			layout:      'fit',
			items:       {
				xtype:     'panel',
				border:    false,
				contentEl: 'deleteTeachingForm'
			},
			listeners: {
				'beforehide':function(window){
					document.getElementById("whatTeachingToDelete").value='';
					document.getElementById("deleteTeachingFormStatus").innerHTML='';
				},
				'beforeshow':function(window){
					document.getElementById("whatTeachingToDelete").value=teachingExtWrapper.panel.getSelectionModel().getLastSelected().data.id;
				}
			}
		})
}

var informationAndTeachingWindow = Ext.create('Ext.window.Window', {
	title:       'Informations & Teachings',
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      600,
	width:       900,
	layout:      'border',
	items:[	informationExtWrapper.panel, teachingExtWrapper.panel],
	listeners: {
		'show':function(window){
			informationExtWrapper.panel.store.loadData([]);// TODO: search a better way to clear the grid.
			informationLoadMask.show();
 		    loadInformations();
		}
	}
});

var informationLoadMask = new Ext.LoadMask(informationExtWrapper.panel, {msg:"Loading Informations. Please wait..."});
function afterReceiveInformation(data){
	informationExtWrapper.panel.store.loadData(eval(data))
	informationLoadMask.hide();
}

var teachingLoadMask = new Ext.LoadMask(teachingExtWrapper.panel, {msg:"Loading Teachings. Please wait..."});
function afterReceiveTeachings(data){
	teachingExtWrapper.panel.store.loadData(eval(data))
	teachingLoadMask.hide();
}

var contextMenu = Ext.create('Ext.menu.Menu', {
	items:[
	       { text : 'Add a Knowledge',       handler : function(item){createKnowledgeForm.show();} }, 
		   { text : 'Rename this Knowledge', handler : function(item){updateKnowledgeForm.show();} }, 
		   { text : 'Delete this Knowledge', handler : function(item){deleteKnowledgeForm.show();} }, 
		   '-', 
		   { text : 'Topics & Teachings',    handler : function(item){informationAndTeachingWindow.show();}}
   ]
});

