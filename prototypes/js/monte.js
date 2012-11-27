var redraw;

//////////////
// filters

    var test_filter = "";
    var author_filter = "";
    var dependency_filter = "";

    var show_dependencies = false;
    var show_authors = false;

function getQuerystring2(key, default_) 
{ 
    if (default_==null) 
    { 
        default_=""; 
    } 
    var search = unescape(location.search); 
    if (search == "") 
    { 
        return default_; 
    } 
    search = search.substr(1); 
    var params = search.split("&"); 
    for (var i = 0; i < params.length; i++) 
    { 
        var pairs = params[i].split("="); 
        if(pairs[0] == key) 
        { 
            return new RegExp(pairs[1],'i'); 
        } 
    } 


    return default_; 
}

$(document).ready(function() {

        test_filter = getQuerystring2("test_filter", "");
        author_filter = getQuerystring2("author_filter", "");
        dependency_filter = getQuerystring2("dependency_filter", "");

        show_dependencies = getQuerystring2("show_dependencies", false);
        show_authors = getQuerystring2("show_authors", false);


    var search = unescape(location.search); 
        if (search != "") {
            search = search.substr(1); 
            var params = search.split("&"); 
            for (var i = 0; i < params.length; i++) { 
                var pairs = params[i].split("="); 
                $("#"+pairs[0]).val(pairs[1]);
            }
        } 


    var g;//  = new Graph();
    //g.edgeFactory.template.style.directed = true;

////////////////////////
// render stuff

    var render_test_case = function(r, n) {
        var highlight = test_filter != "" && (n.label || n.id).match(test_filter);
        
        var test_icon = r.path("M15.5,3.029l-10.8,6.235L4.7,21.735L15.5,27.971l10.8-6.235V9.265L15.5,3.029zM24.988,10.599L16,15.789v10.378c0,0.275-0.225,0.5-0.5,0.5s-0.5-0.225-0.5-0.5V15.786l-8.987-5.188c-0.239-0.138-0.321-0.444-0.183-0.683c0.138-0.238,0.444-0.321,0.683-0.183l8.988,5.189l8.988-5.189c0.238-0.138,0.545-0.055,0.684,0.184C25.309,10.155,25.227,10.461,24.988,10.599z");

        var set = r.set()
                .push(

                    test_icon
                        .translate(n.point[0]-18, n.point[1]-28)
                        .scale(0.75,0.75)
                        .attr(
                            {
                                "fill": "#feb", 
                                r : "12px", 
                                "stroke-width" : (highlight) ? "2px" : "1px" 
                            }
                        )
                    )
                .push(
                    r.text(n.point[0], n.point[1] + 11, (n.label || n.id).replace("_Test.php","").replace("_","\n") )
                )
        /*set.mouseup(function() { 
            alert("FUU!!") 
        })*/

        return set;
    };
    var render_author = function(r, n) {
        var text = (n.label || n.id).split("@")[0].replace('.','\n');
        var highlight = author_filter != "" && text.match(author_filter);
        var head_icon = r.path("M20.771,12.364c0,0,0.849-3.51,0-4.699c-0.85-1.189-1.189-1.981-3.058-2.548s-1.188-0.454-2.547-0.396c-1.359,0.057-2.492,0.792-2.492,1.188c0,0-0.849,0.057-1.188,0.397c-0.34,0.34-0.906,1.924-0.906,2.321s0.283,3.058,0.566,3.624l-0.337,0.113c-0.283,3.283,1.132,3.68,1.132,3.68c0.509,3.058,1.019,1.756,1.019,2.548s-0.51,0.51-0.51,0.51s-0.452,1.245-1.584,1.698c-1.132,0.452-7.416,2.886-7.927,3.396c-0.511,0.511-0.453,2.888-0.453,2.888h26.947c0,0,0.059-2.377-0.452-2.888c-0.512-0.511-6.796-2.944-7.928-3.396c-1.132-0.453-1.584-1.698-1.584-1.698s-0.51,0.282-0.51-0.51s0.51,0.51,1.02-2.548c0,0,1.414-0.397,1.132-3.68H20.771z")

        var set = r.set()
            .push (
                    head_icon
                    .translate(n.point[0]-18, n.point[1]-28)
                    .scale(0.75,0.75)
                    .attr(
                            {
                                "fill": "#606", 
                                "stroke-width" : (highlight) ? "2px" : "1px",
                            }
                        )
                )
            .push(
                r.text(n.point[0], n.point[1] + 11, text )
            )

        return set;    
    }
    var render_class = function(r, n) {
        var text = (n.label || n.id);
        var parts = text.split("_")
        var class_name = parts[parts.length-1];
        var highlight = dependency_filter != "" && text.match(dependency_filter);

        var class_icon = r.path("M23.024,5.673c-1.744-1.694-3.625-3.051-5.168-3.236c-0.084-0.012-0.171-0.019-0.263-0.021H7.438c-0.162,0-0.322,0.063-0.436,0.18C6.889,2.71,6.822,2.87,6.822,3.033v25.75c0,0.162,0.063,0.317,0.18,0.435c0.117,0.116,0.271,0.179,0.436,0.179h18.364c0.162,0,0.317-0.062,0.434-0.179c0.117-0.117,0.182-0.272,0.182-0.435V11.648C26.382,9.659,24.824,7.49,23.024,5.673zM25.184,28.164H8.052V3.646h9.542v0.002c0.416-0.025,0.775,0.386,1.05,1.326c0.25,0.895,0.313,2.062,0.312,2.871c0.002,0.593-0.027,0.991-0.027,0.991l-0.049,0.652l0.656,0.007c0.003,0,1.516,0.018,3,0.355c1.426,0.308,2.541,0.922,2.645,1.617c0.004,0.062,0.005,0.124,0.004,0.182V28.164z");

        var set = r.set()
            .push (
                    class_icon.
                        translate(n.point[0]-18, n.point[1]-28)
                        .scale(0.75,0.75)
                        .attr(
                            {
                                "fill": "#060", 
                                r : "12px", 
                                "stroke-width" : (highlight) ? "2px" : "1px" 
                            }
                        )
                    )   
            .push(
                    r.text(n.point[0], n.point[1] + 10, class_name )
                        /*.attr({
                            "fill": (highlight) ? "red" : "black",
                        })*/
            )

            return set;
        };

    var filter_data = function() {
        $("#canvas").empty();
        g = new Graph()
        var was_used = {};
        for (var test in data.tests) {
            var skip = false;
            skip = ("" != test_filter && !test.match(test_filter))

            var current = data.tests[test];
            
            if ("" != author_filter && !skip) {
                skip = true;
                for (var i = 0; i < current.authors.length; i++) {
                    if (current.authors[i].match(author_filter)) {
                        skip = false; break;
                    }
                }   
            }
            
            if ("" != dependency_filter && !skip) {
                skip = true;
                var deps = current.sixt_dependencies;
                for (var t in deps) {
                    if (!skip) { break; }
                    for (var i = 0; i < deps[t].length; i++) {
                        if (deps[t][i].match(dependency_filter)) {
                            skip = false; break;
                        }
                    }
                }  
            }

            if (skip) {
                continue;
            }

            g.addNode(test, {
                render:render_test_case
            });
            
            if (show_authors) {
                for (var i = 0; i < current.authors.length; i++) {
                    g.addNode(current.authors[i], {render: render_author})
                    g.addEdge(test, current.authors[i]);
                }
            }

            if (show_dependencies) {
                var deps = current.sixt_dependencies;
                for (var t in deps) {
                    for (var i = 0; i < deps[t].length; i++) {
                        if (deps[t][i] != "") {
                            if (!was_used[deps[t][i]]) {
                                    g.addNode(deps[t][i], {
                                        render:render_class
                                    }
                                );
                                was_used[deps[t][i]] = true;                    
                            }
                            g.addEdge(test, deps[t][i], {
                                display: 'none'
                            } )
                        }
                    }
                }
            }
            
        }
    }
    
    redraw = function() {
        filter_data();

        var width = $(document).width();
        var height = $(document).height() - 25;

        //var layouter = new Graph.Layout.Ordered(g, topological_sort(g));
        
        layouter = new Graph.Layout.Spring(g);
        renderer = new Graph.Renderer.Raphael('canvas', g, width, height);

        layouter.layout();
        renderer.draw();
    };


    redraw();
});