using System;
using System.Collections.Generic;

namespace WpfDesktopApplicationv2.Stores
{
    public class BroadcastLedSelectedStore
    {
        public event EventHandler<KeyValuePair<int, int>> LedSelected;

        public void OnLedSelected(KeyValuePair<int, int> coordinates)
        {
            LedSelected?.Invoke(this, coordinates);
        }
    }
}
