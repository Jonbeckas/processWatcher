#[macro_use] extern crate log;
#[macro_use] extern crate serde_derive;
extern crate serde;
extern crate serde_json;
use std::fs::File;
use std::io::{BufReader};
use std::error::Error;
use std::process::exit;
use std::path::{Path, PathBuf};
use std::{fs, thread, time};
use std::borrow::Borrow;
use crate::structs::{Config, Workstruct};
mod structs;
mod processes;

fn main(){
    env_logger::init();
    info!("ProcessWatcher started");
    let path = get_path();
    let path:&Path =path.borrow();
    info!("Checking Configuration path: {}",path.display());
    loop {
        match create_file(path) {
            Err(e) => {
                error!("{:?}", e.to_string());
                exit(1)
            },
            _ => {}
        }
        match read_data_from_json(path) {
            Ok(jso) =>match run_json(jso.work) {
                Ok(()) =>(),
                Err(e) => error!("{}",e.to_string())
            },
            Err(e) => {
                error!("Error while parsing the json: {:?}", e.to_string());
                exit(1);
            }
        };
        thread::sleep(time::Duration::from_secs(60));
    }

}
fn run_json(json:Vec<Workstruct>)-> Result<(),Box<dyn Error>> {
    for js in json {
        if !js.procname.is_none() {
            if processes::check_running(js.procname.clone().unwrap()) {
                info!("{} is running",js.procname.unwrap());
                if !js.is_running.is_none() {
                    info!("Start {}",js.is_running.clone().unwrap());
                    match processes::start_process(js.is_running.unwrap()) {
                        Ok(res)=> info!("{}",res),
                        Err(e) => error!("Failed to start process: {}",e.to_string())
                    };
                }
            } else {
                info!("{} is not running!",js.procname.unwrap());
                if !js.not_running.is_none() {
                    info!("Start {}",js.not_running.clone().unwrap());
                    match processes::start_process(js.not_running.unwrap()) {
                        Ok(res)=> info!("{}",res),
                        Err(e) => error!("Failed to start process: {}",e.to_string())
                    };
                }
            }
        }
        /*if !js.linkconf.is_none() {
            let mut path_buf = PathBuf::new();
            path_buf.push(js.linkconf.unwrap());
            let path:&Path = path_buf.as_path();
            match create_file(path) {
                Err(e)=> error!("Cannot create file: {}",e.to_string()),
                _ => {}
            }
            match read_data_from_json(path) {
                Ok(json)=> match run_json(json) {
                    Ok(())=>(),
                    Err(e)=> error!("{}",e.to_string())
                }
                Err(e)=> error!("Error while parsing the json: {:?}", e.to_string())
            }
        }*/
    }
    Ok(())
}

fn get_path()-> Box<Path> {
    let folder =  std::env::current_exe().ok().expect("Error while open configuration Folder");
    let folder = folder.parent().unwrap();
    let mut path_buf = PathBuf::new();
    path_buf.push(folder.parent().unwrap());
    path_buf.push("config");
    path_buf.set_extension("json");
    return Box::from(path_buf.as_path());
}

fn create_file(path:&Path) ->Result<(),Box<dyn Error>>{
    std::fs::create_dir_all(path.parent().unwrap())?;
    if !path.exists() {
        File::create(path)?;
        fs::write(path,"[]")?;
        info!("Configuration file at {} does not exist, creating one", path.to_str().unwrap());
    }
    Ok(())
}



fn read_data_from_json(path:&Path)-> Result<Config,Box<dyn Error>> {
    let file = File::open(path)?;
    let reader = BufReader::new(file);

    // Read the JSON contents of the file as an instance of `User`.
    let u:Config = serde_json::from_reader(reader)?;
    Ok(u)
}


