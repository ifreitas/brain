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

//function jitInit(){
    //init data
//    var json = {
//        id: "node02",
//        name: "Inteligencia Artificial",
//        data: {},
//        children:[]
//        children: [{
//            id: "node13",
//            name: "1.3",
//            data: {},
//            children: [{
//                id: "node24",
//                name: "2.4",
//                data: {},
//                children: [{
//                    id: "node35",
//                    name: "3.5",
//                    data: {},
//                    children: [{
//                        id: "node46",
//                        name: "4.6",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node37",
//                    name: "3.7",
//                    data: {},
//                    children: [{
//                        id: "node48",
//                        name: "4.8",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node49",
//                        name: "4.9",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node410",
//                        name: "4.10",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node411",
//                        name: "4.11",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node312",
//                    name: "3.12",
//                    data: {},
//                    children: [{
//                        id: "node413",
//                        name: "4.13",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node314",
//                    name: "3.14",
//                    data: {},
//                    children: [{
//                        id: "node415",
//                        name: "4.15",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node416",
//                        name: "4.16",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node417",
//                        name: "4.17",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node418",
//                        name: "4.18",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node319",
//                    name: "3.19",
//                    data: {},
//                    children: [{
//                        id: "node420",
//                        name: "4.20",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node421",
//                        name: "4.21",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node222",
//                name: "2.22",
//                data: {},
//                children: [{
//                    id: "node323",
//                    name: "3.23",
//                    data: {},
//                    children: [{
//                        id: "node424",
//                        name: "4.24",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }]
//        }, {
//            id: "node125",
//            name: "1.25",
//            data: {},
//            children: [{
//                id: "node226",
//                name: "2.26",
//                data: {},
//                children: [{
//                    id: "node327",
//                    name: "3.27",
//                    data: {},
//                    children: [{
//                        id: "node428",
//                        name: "4.28",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node429",
//                        name: "4.29",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node330",
//                    name: "3.30",
//                    data: {},
//                    children: [{
//                        id: "node431",
//                        name: "4.31",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node332",
//                    name: "3.32",
//                    data: {},
//                    children: [{
//                        id: "node433",
//                        name: "4.33",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node434",
//                        name: "4.34",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node435",
//                        name: "4.35",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node436",
//                        name: "4.36",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node237",
//                name: "2.37",
//                data: {},
//                children: [{
//                    id: "node338",
//                    name: "3.38",
//                    data: {},
//                    children: [{
//                        id: "node439",
//                        name: "4.39",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node440",
//                        name: "4.40",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node441",
//                        name: "4.41",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node342",
//                    name: "3.42",
//                    data: {},
//                    children: [{
//                        id: "node443",
//                        name: "4.43",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node344",
//                    name: "3.44",
//                    data: {},
//                    children: [{
//                        id: "node445",
//                        name: "4.45",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node446",
//                        name: "4.46",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node447",
//                        name: "4.47",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node348",
//                    name: "3.48",
//                    data: {},
//                    children: [{
//                        id: "node449",
//                        name: "4.49",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node450",
//                        name: "4.50",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node451",
//                        name: "4.51",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node452",
//                        name: "4.52",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node453",
//                        name: "4.53",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node354",
//                    name: "3.54",
//                    data: {},
//                    children: [{
//                        id: "node455",
//                        name: "4.55",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node456",
//                        name: "4.56",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node457",
//                        name: "4.57",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node258",
//                name: "2.58",
//                data: {},
//                children: [{
//                    id: "node359",
//                    name: "3.59",
//                    data: {},
//                    children: [{
//                        id: "node460",
//                        name: "4.60",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node461",
//                        name: "4.61",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node462",
//                        name: "4.62",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node463",
//                        name: "4.63",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node464",
//                        name: "4.64",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }]
//        }, {
//            id: "node165",
//            name: "1.65",
//            data: {},
//            children: [{
//                id: "node266",
//                name: "2.66",
//                data: {},
//                children: [{
//                    id: "node367",
//                    name: "3.67",
//                    data: {},
//                    children: [{
//                        id: "node468",
//                        name: "4.68",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node469",
//                        name: "4.69",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node470",
//                        name: "4.70",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node471",
//                        name: "4.71",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node372",
//                    name: "3.72",
//                    data: {},
//                    children: [{
//                        id: "node473",
//                        name: "4.73",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node474",
//                        name: "4.74",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node475",
//                        name: "4.75",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node476",
//                        name: "4.76",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node377",
//                    name: "3.77",
//                    data: {},
//                    children: [{
//                        id: "node478",
//                        name: "4.78",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node479",
//                        name: "4.79",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node380",
//                    name: "3.80",
//                    data: {},
//                    children: [{
//                        id: "node481",
//                        name: "4.81",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node482",
//                        name: "4.82",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node283",
//                name: "2.83",
//                data: {},
//                children: [{
//                    id: "node384",
//                    name: "3.84",
//                    data: {},
//                    children: [{
//                        id: "node485",
//                        name: "4.85",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node386",
//                    name: "3.86",
//                    data: {},
//                    children: [{
//                        id: "node487",
//                        name: "4.87",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node488",
//                        name: "4.88",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node489",
//                        name: "4.89",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node490",
//                        name: "4.90",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node491",
//                        name: "4.91",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node392",
//                    name: "3.92",
//                    data: {},
//                    children: [{
//                        id: "node493",
//                        name: "4.93",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node494",
//                        name: "4.94",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node495",
//                        name: "4.95",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node496",
//                        name: "4.96",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node397",
//                    name: "3.97",
//                    data: {},
//                    children: [{
//                        id: "node498",
//                        name: "4.98",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node399",
//                    name: "3.99",
//                    data: {},
//                    children: [{
//                        id: "node4100",
//                        name: "4.100",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4101",
//                        name: "4.101",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4102",
//                        name: "4.102",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4103",
//                        name: "4.103",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node2104",
//                name: "2.104",
//                data: {},
//                children: [{
//                    id: "node3105",
//                    name: "3.105",
//                    data: {},
//                    children: [{
//                        id: "node4106",
//                        name: "4.106",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4107",
//                        name: "4.107",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4108",
//                        name: "4.108",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node2109",
//                name: "2.109",
//                data: {},
//                children: [{
//                    id: "node3110",
//                    name: "3.110",
//                    data: {},
//                    children: [{
//                        id: "node4111",
//                        name: "4.111",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4112",
//                        name: "4.112",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node3113",
//                    name: "3.113",
//                    data: {},
//                    children: [{
//                        id: "node4114",
//                        name: "4.114",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4115",
//                        name: "4.115",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4116",
//                        name: "4.116",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node3117",
//                    name: "3.117",
//                    data: {},
//                    children: [{
//                        id: "node4118",
//                        name: "4.118",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4119",
//                        name: "4.119",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4120",
//                        name: "4.120",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4121",
//                        name: "4.121",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node3122",
//                    name: "3.122",
//                    data: {},
//                    children: [{
//                        id: "node4123",
//                        name: "4.123",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4124",
//                        name: "4.124",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node2125",
//                name: "2.125",
//                data: {},
//                children: [{
//                    id: "node3126",
//                    name: "3.126",
//                    data: {},
//                    children: [{
//                        id: "node4127",
//                        name: "4.127",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4128",
//                        name: "4.128",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4129",
//                        name: "4.129",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }]
//        }, {
//            id: "node1130",
//            name: "1.130",
//            data: {},
//            children: [{
//                id: "node2131",
//                name: "2.131",
//                data: {},
//                children: [{
//                    id: "node3132",
//                    name: "3.132",
//                    data: {},
//                    children: [{
//                        id: "node4133",
//                        name: "4.133",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4134",
//                        name: "4.134",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4135",
//                        name: "4.135",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4136",
//                        name: "4.136",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4137",
//                        name: "4.137",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }, {
//                id: "node2138",
//                name: "2.138",
//                data: {},
//                children: [{
//                    id: "node3139",
//                    name: "3.139",
//                    data: {},
//                    children: [{
//                        id: "node4140",
//                        name: "4.140",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4141",
//                        name: "4.141",
//                        data: {},
//                        children: []
//                    }]
//                }, {
//                    id: "node3142",
//                    name: "3.142",
//                    data: {},
//                    children: [{
//                        id: "node4143",
//                        name: "4.143",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4144",
//                        name: "4.144",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4145",
//                        name: "4.145",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4146",
//                        name: "4.146",
//                        data: {},
//                        children: []
//                    }, {
//                        id: "node4147",
//                        name: "4.147",
//                        data: {},
//                        children: []
//                    }]
//                }]
//            }]
//        }]
//    };
    //end
 
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
        
//        Navigation: {
//          enable:true,
//          panning:true//, // TODO requer ajuste no jit.js. VER: https://groups.google.com/forum/#!searchin/javascript-information-visualization-toolkit/mouse$20position|sort:date/javascript-information-visualization-toolkit/hGyn-Cvsn7g/K2fxD8XRD7kJ
//          zooming: 50     // TODO requer ajuste no jit.js pois o node propriamente dito(a parte clic�vel) n�o cresce nem dimimui junto com o zoom. 
//        },
        
//        Tips: {
//            enable: true,
//            offsetX: 20,
//            offsetY: 20,
//            onShow: function(tip, node, isLeaf, domElement) {
//              var html = "<div class=\"tip-title\"><b>" + node.name 
//               + "</b></div><div class=\"tip-text\">Topics</div><ul><li>Topic One</li><li>Topic Two</li><li>Topic Three</li></ul>";
//                //var data = node.data;
//                //if(data.playcount) {
//                //  html += "play count: " + data.playcount;
//                //}
//                //if(data.image) {
//                //  html += "<img src=\""+ data.image +"\" class=\"album\" />";
//                //}
//              tip.innerHTML =  html; 
//            }  
//          },

        
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
        	//document.onclick= function(){alert(1);return false}
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
			        //collapsible: true,
			        //split: true,
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
			        //collapsible: true,
			        //split: true,
			        width: 300,
			        margins: '0 5 0 5',
			        items:[
				        Ext.create('Ext.tab.Panel', {
					    	region: 'center',
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

var createKowledgeWindow = Ext.create('Ext.window.Window', {
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
var updateKowledgeWindow = Ext.create('Ext.window.Window', {
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
var deleteKowledgeWindow = Ext.create('Ext.window.Window', {
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

var teachingWindow = Ext.create('Ext.window.Window', {
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
		contentEl: 'teachingWindow'
	}
});

var informationWindow = Ext.create('Ext.window.Window', {
	title:       'Informations & Teachings',// + ObjectManager.getLastClicked().name,
	closeAction: 'hide',
	modal:       true,
	closable:    true,
	resizable:	 false,
	height:      600,
	width:       900,
	layout:      'border',
	items:[
	       {
	    	   region: 'north',
	    	   margins: '5 5 5 5',
	    	   title: 'Informations',
	    	   height: 200,
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
	    	   ]
	       },
	       {
	    	   region: 'center',
	    	   margins: '0 5 5 5',
	    	   title: 'Teachings of the selected Information',
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
	    	   ]
	       }
	]
});

var contextMenu = Ext.create('Ext.menu.Menu', {
	items:[
	       {
	    	   text : 'Add a Knowledge',
	    	   handler : function(item){createKowledgeWindow.show();}
		   }, 
		   {
			   text : 'Rename this Knowledge',
	    	   handler : function(item){updateKowledgeWindow.show();}
		   }, 
		   {
			   text : 'Delete this Knowledge',
	    	   handler : function(item){deleteKowledgeWindow.show();}
		   }, 
		   '-', 
		   {
			   text : 'Topics & Teachings',
	    	   handler : function(item){informationWindow.show();}
		   }
   ]
});

