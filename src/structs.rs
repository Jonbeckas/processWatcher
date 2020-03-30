extern crate serde;
extern crate serde_json;

#[derive(Serialize, Deserialize, Debug)]
pub struct Workstruct {
    #[serde(skip_serializing_if = "Option::is_none")]
    pub is_running:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub not_running:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub procname:Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub linkconf:Option<String>,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Config {
    pub attemps: u32,
    pub monitor:bool,
    pub work: Vec<Workstruct>
}