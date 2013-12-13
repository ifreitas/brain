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

var teachingForm = Ext.create('Ext.window.Window', {
	title:       'Create the Teaching',
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      400,
	width:       500,
	layout:      'fit',
	items:       {
		xtype:     'panel',
		border:    false,
		contentEl: 'teachingForm'
	}
});

var informationForm = Ext.create('Ext.window.Window', {
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
			document.getElementById("informationNameInput").form.reset();
			document.getElementById("informationFormStatus").innerHTML='';
		},
		'beforeshow':function(window){
			document.getElementById("informationKnowledgeId").value=ObjectManager.lastClicked.id;
		}
	}
});

var deleteInformationForm = Ext.create('Ext.window.Window', {
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
			document.getElementById("whatInformationToDelete").form.reset();
			document.getElementById("deleteInformationFormStatus").innerHTML='';
		},
		'beforeshow':function(window){
			document.getElementById("whatInformationToDelete").value=informationPanel.getSelectionModel().getLastSelected().data.id;
		}
	}
});

var informationStore = Ext.create('Ext.data.Store', {
    storeId:'informationStore',
    fields:['id', 'name', 'knowledgeId'],
    data:[]
});

var informationPanel = Ext.create('Ext.grid.Panel', {
	   region: 'north',
	   margins: '5 5 5 5',
	   height: 205,
	   title: 'Informations',
	   store: informationStore,
	   tbar: [
			{
				  text: 'Create',
				  handler:function(){
					  informationForm.show();
				  }
			},
			{
				  text: 'Update',
				  handler:function(){
					  selectedInformation = informationPanel.getSelectionModel().getLastSelected().data
					  Ext.getElementById("informationId").value= selectedInformation.id;
					  Ext.getElementById("informationNameInput").value= selectedInformation.name;
					  Ext.getElementById("informationKnowledgeId").value=selectedInformation.knowledgeId;
					  informationForm.show();
				  }
			},
			{
				  text: 'Delete',
				  handler:function(){
					  selectedInformation = informationPanel.getSelectionModel().getLastSelected().data
					  Ext.getElementById("whatInformationToDelete").value= selectedInformation.id;
					  deleteInformationForm.show();
				  }
			}
		],
	    columns: [
	        { text: 'Name',  dataIndex: 'name', width:'100%'}
	    ]
});

var informationAndTeachingWindow = Ext.create('Ext.window.Window', {
	title:       'Informations & Teachings',// + ObjectManager.getLastClicked().name,
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      600,
	width:       900,
	layout:      'border',
	items:[	informationPanel
	    	   ,
	    	   Ext.create('Ext.grid.Panel', {
	    		   region: 'center',
	    		   layout: 'fit',
	    		   margins: '0 5 5 5',
	    		   title: 'Teachings',
	    		   tbar: [
	    		          {
	    		        	  text: 'Create'
	    		          },
	    		          {
	    		        	  text: 'Update'
	    		          },
	    		          {
	    		        	  text: 'Delete'
	    		          }
	    		          ],
	    		   columns: [
		                    { text: 'When the user says',  dataIndex: 'whenTheUserSays', width:280},
		                    { text: 'Responding to',  dataIndex: 'respondigTo', width:200 },
		                    { text: 'Memorize',  dataIndex: 'memorize', width:100},
		                    { text: 'Say',  dataIndex: 'say', width:280 },
		           ],
		           height: 200
	    	   })
	],
	listeners: {
		'show':function(window){
			informationStore.loadData([]);// TODO: search a better way to clear the grid.
			informationAndTeachingLoadMask.show();
 		    loadInformations();
		}
	}
});

var informationAndTeachingLoadMask = new Ext.LoadMask(informationAndTeachingWindow, {msg:"Loading Informations. Please wait..."});

function afterReceiveInformation(data){
	informationStore.loadData(eval(data))
	informationAndTeachingLoadMask.hide();
}



var contextMenu = Ext.create('Ext.menu.Menu', {
	items:[
	       {
	    	   text : 'Add a Knowledge',
	    	   handler : function(item){createKnowledgeForm.show();}
		   }, 
		   {
			   text : 'Rename this Knowledge',
	    	   handler : function(item){updateKnowledgeForm.show();}
		   }, 
		   {
			   text : 'Delete this Knowledge',
	    	   handler : function(item){deleteKnowledgeForm.show();}
		   }, 
		   '-', 
		   {
			   text : 'Topics & Teachings',
	    	   handler : function(item){informationAndTeachingWindow.show();}
		   }
   ]
});

