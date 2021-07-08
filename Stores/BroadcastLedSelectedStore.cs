using System;
using System.Collections.Generic;

namespace WpfDesktopApplicationv2.Stores
{
    public class BroadcastLedSelectedStore
    {
        public event EventHandler<KeyValuePair<uint, uint>> LedSelected;

        public void OnLedSelected(KeyValuePair<uint, uint> coordinates)
        {
            LedSelected?.Invoke(this, coordinates);
        }
    }
}
