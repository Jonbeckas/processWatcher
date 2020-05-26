extern crate serde;
extern crate serde_json;


#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Workstruct {
    #[serde(skip_serializing_if = "Option::is_none")]
    pub is_running:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub not_running:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub procname:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub linkconf:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub keepalive:Option<String>,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Config {
    #[serde(skip_serializing_if = "Option::is_none")]
    pub attemps: Option<u64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub refresh:Option<u64>,
    pub work: Vec<Workstruct>
}


pub static  NEWJSON :&str= "{\"work\": []}";
