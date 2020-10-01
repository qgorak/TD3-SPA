package edu.td3SPA;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import io.github.jeemv.springboot.vuejs.AbstractVueJS;




@Controller



 
public class OrgaController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AbstractVueJS vue;
    
	@RequestMapping("/orgas/")
    public String index(ModelMap model) {
		
		//Get Organizations list from API
	    final String URL = "http://localhost:8081/rest/orgas/";
		ResponseEntity<List<ModelMap>> responseEntity = restTemplate.exchange(
			    URL, 
			    HttpMethod.GET, 
			    null, 
			    new ParameterizedTypeReference<List<ModelMap>>() {
			    });
		List<ModelMap> Organizations = responseEntity.getBody();
        
		//Adding Organizations list to vue's data
	    vue.addData("organizations", Organizations);
	    //Adding Required data to vue
	    AddDatas();
	   //Adding Required computed to vue
		vue.addComputed("formTitle", "return this.editedIndex === -1 ? 'Nouvelle Organisation' : 'Editer Organisation'");
		//Adding Required methods to vue
		AddMethods();
	    model.put("vue", vue);
        return "index";
       }
	
	public void AddDatas() {
		vue.addDataRaw("headers", " [{\r\n" + 
				"                        \"text\": \"Organizations\",\r\n" + 
				"                        \"align\": \"start\",\r\n" + 
				"                        \"sortable\": false,\r\n" + 
				"                        \"value\": \"name\"\r\n" + 
				"                    }, {\r\n" + 
				"                        \"text\": \"Aliases\",\r\n" + 
				"                        \"value\": \"aliases\"\r\n" + 
				"                    }, {\r\n" + 
				"                        \"text\": \"Domain\",\r\n" + 
				"                        \"value\": \"domain\"\r\n" + 
				"                    }, {\r\n" + 
				"                        \"text\": \"Actions\",\r\n" + 
				"                        \"value\": \"actions\"\r\n" + 
				"                    }, {\r\n" + 
				"                        \"value\": \"data-table-expand\"\r\n" + 
				"                    }]");
		
		vue.addDataRaw("editedItem", "{ name: '',aliases: '', domain: '',settings: '',users: []}");
		vue.addData("dialog",false);
		vue.addDataRaw("defaultItem", "{ name: '',aliases: '', domain: '',settings: '',users: []}");
		vue.addData("editedIndex",-1);
	}
	
	public void AddMethods() {
		
		vue.addMethod("editItem" , "this.editedIndex = this.organizations.indexOf(item)\r\n" + 
				"      this.editedItem = Object.assign({}, item)\r\n" + 
				"      this.dialog = true","item");

		vue.addMethod("save" , "if (this.editedIndex > -1) {\r\n"
				+ "        Object.assign(this.organizations[this.editedIndex], this.editedItem)\r\n"
				+ "        this.$http['post']('http://localhost:8081/rest/orgas/update/'+ this.organizations[this.editedIndex].id,this.editedItem)"
				+ "      } else {\r\n"	
				+ "        this.organizations.push(this.editedItem)\r\n"
				+ "        this.$http['post']('http://localhost:8081/rest/orgas/create', this.editedItem)"
				+ "      }\r\n"
				+ "      this.close()");
		vue.addMethod("close" , " this.dialog = false\r\n"
				+ "      this.$nextTick(() => {\r\n"
				+ "        this.editedItem = Object.assign({}, this.defaultItem)\r\n"
				+ "        this.editedIndex = -1\r\n"
				+ "      })");
		vue.addMethod("deleteItem" , " const index = this.organizations.indexOf(item)\r\n"
				+ "      confirm('Are you sure you want to delete this item?') && this.organizations.splice(index, 1) && this.$http['delete']('http://localhost:8081/rest/orgas/delete/'+ item.id)","item");
		
		
	}
	
	
}