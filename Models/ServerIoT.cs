using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.Models
{
    public class ServerIoT
    {
        private string ip;
        private ErrorStore _error;

        public ServerIoT(string _ip, ErrorStore err)
        {
            ip = _ip;
            _error = err;
        }

        
        private string GetFileUrl()
        {
            return "http://" + ip + "/IOT_server/sensors_via_deamon.php?id=env";
        }
        private string GetFileUrlPostLeds()
        {
            return "http://" + ip + "/IOT_server/ledmatrix_via_deamon.php";
        }

        private string GetFileUrlAngles()
        {
            return "http://" + ip + "/IOT_server/sensors_via_deamon.php?id=ori";
        }

        


        /// <summary>
        /// Gets the data from server about measurements in form of json string.
        /// </summary>
        /// <returns></returns>
        public async Task<string> GETwithClient()
        {
            string responseText = null;

            try
            {
                using (HttpClient client = new HttpClient())
                {
                    responseText = await client.GetStringAsync(GetFileUrl());
                    _error.ErrorState = "Connection succesful";
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("NETWORK ERROR");
                Debug.WriteLine(e);
                _error.ErrorState = e.ToString();
            }

            return responseText;
        }

        public async Task<string> GETwithClientRPY()
        {
            string responseText = null;

            try
            {
                using (HttpClient client = new HttpClient())
                {
                    responseText = await client.GetStringAsync(GetFileUrlAngles());
                    _error.ErrorState = "Connection succesful";
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("NETWORK ERROR");
                Debug.WriteLine(e);
                _error.ErrorState = e.ToString();
            }

            return responseText;
        }

        

        

        /// <summary>
        /// Post data about led matrix state with client
        /// </summary>
        /// <param name="data">Data in necessary form that will be sent to server.</param>
        /// <returns></returns>
        public async Task<string> POSTwithClient(Dictionary<string, List<int>> data)
        {
            string responseText = null;

            try
            {
                using (HttpClient client = new HttpClient())
                {
                    // POST request data
                    string stringContent = JsonConvert.SerializeObject(data);
                    var httpContent = new StringContent(
                        stringContent, 
                        Encoding.UTF8, 
                        "application/json"
                        );
                    // Send POST request
                    var result = await client.PostAsync(GetFileUrlPostLeds(), httpContent);
                    // Read response content
                    responseText = await result.Content.ReadAsStringAsync();
                    _error.ErrorState = "Connection succesful";
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("NETWORK ERROR");
                Debug.WriteLine(e);
                _error.ErrorState = e.ToString();
            }

            return responseText;
        }

        
    }
}
